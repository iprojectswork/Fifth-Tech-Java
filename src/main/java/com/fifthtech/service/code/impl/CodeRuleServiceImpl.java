package com.fifthtech.service.code.impl;

import com.fifthtech.service.code.CodeRuleService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.code.CodeRule;
import com.fifthtech.dao.entity.code.CodeSequence;
import com.fifthtech.dao.mapper.code.CodeRuleMapper;
import com.fifthtech.dao.mapper.code.CodeSequenceMapper;
import com.fifthtech.dto.code.CodeRuleDTO;
import com.fifthtech.dto.code.CodeRuleQueryDTO;
import com.fifthtech.dto.code.CodeSegmentDTO;
import com.fifthtech.dto.code.CodeSequenceQueryDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * CodeRuleServiceImpl
 *
 * <ul>
 *   <li>片段校验：恰好 1 个 SEQUENCE、至多 1 个 DATE、{@code batch_size} 1~5000、
 *   SEQUENCE.length 1~18、SEQUENCE.step &gt; 0、DATE.pattern 白名单。</li>
 *   <li>segments 以 Jackson {@code List<CodeSegmentDTO>} 双向转换；落入 PG JSONB 列。</li>
 *   <li>status / segments / batch_size 变更时按规则可能产生的 periodKey 全集删 Redis pool
 *   （仅清理，不删 sequence，sequence 行由 sys_code_sequence 永久保留）。</li>
 * </ul>
 *
 * @author RH
 * @description 编码规则服务实现
 * @date 2026-08-02
 */
@Service
public class CodeRuleServiceImpl extends ServiceImpl<CodeRuleMapper, CodeRule> implements CodeRuleService {

    /** DATE.pattern 白名单（与 C2-CODE-RULE.md §4.1 一致） */
    private static final Set<String> DATE_PATTERN_WHITELIST;

    static {
        Set<String> s = new HashSet<>();
        s.add("yy");
        s.add("yyyy");
        s.add("yyMM");
        s.add("yyyyMM");
        s.add("yyMMdd");
        s.add("yyyyMMdd");
        DATE_PATTERN_WHITELIST = Collections.unmodifiableSet(s);
    }

    private static final Pattern RULE_CODE_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{0,63}$");

    @Resource
    private CodeSequenceMapper codeSequenceMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${code.generate.default-pool-batch-size:100}")
    private int defaultPoolBatchSize;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<CodeRule> list(CodeRuleQueryDTO query) {
        CodeRuleQueryDTO q = query == null ? new CodeRuleQueryDTO() : query;
        String ruleCode = q.getRuleCode();
        String ruleName = q.getRuleName();
        Integer status = q.getStatus();
        int pageNum = (q.getCurrent() == null || q.getCurrent() < 1) ? 1 : q.getCurrent();
        int pageSize = (q.getSize() == null || q.getSize() < 1) ? 10 : q.getSize();
        LambdaQueryWrapper<CodeRule> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(CodeRule::getDeleted, 0);
        if (ruleCode != null && !ruleCode.isEmpty()) {
            countWrapper.like(CodeRule::getRuleCode, ruleCode);
        }
        if (ruleName != null && !ruleName.isEmpty()) {
            countWrapper.like(CodeRule::getRuleName, ruleName);
        }
        if (status != null) {
            countWrapper.eq(CodeRule::getStatus, status);
        }
        long total = count(countWrapper);
        Page<CodeRule> page = new Page<>(pageNum, pageSize, total);
        if (total == 0) {
            page.setRecords(Collections.emptyList());
            return page;
        }
        q.setOffset((pageNum - 1) * pageSize);
        q.setLimit(pageSize);
        List<CodeRule> records = baseMapper.selectPageList(q);
        page.setRecords(records);
        return page;
    }

    @Override
    public CodeRule info(Long id) {
        if (id == null) {
            return null;
        }
        return getById(id);
    }

    @Override
    public CodeRule infoByRuleCode(String ruleCode) {
        if (ruleCode == null || ruleCode.isEmpty()) {
            return null;
        }
        return baseMapper.selectByRuleCode(ruleCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CodeRule insert(CodeRuleDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        validateRuleCode(dto.getRuleCode());
        validateSegments(dto.getSegments());
        int batchSize = normalizeBatchSize(dto.getBatchSize());
        if (baseMapper.selectByRuleCode(dto.getRuleCode()) != null) {
            throw new IllegalArgumentException("规则编码已存在");
        }
        CodeRule entity = new CodeRule();
        entity.setRuleCode(dto.getRuleCode());
        entity.setRuleName(dto.getRuleName());
        entity.setSegmentsJson(serializeSegments(dto.getSegments()));
        entity.setBatchSize(batchSize);
        entity.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        entity.setRemark(dto.getRemark());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        Long uid = UserContextRef.currentUserIdOrNull();
        if (uid != null) {
            entity.setCreateId(uid);
            entity.setUpdateId(uid);
        }
        save(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CodeRule edit(CodeRuleDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        CodeRule existing = getById(dto.getId());
        if (existing == null) {
            throw new IllegalArgumentException("规则不存在");
        }
        // 唯一性：rule_code 改成已存在的另一条记录的 code
        if (dto.getRuleCode() != null && !dto.getRuleCode().equals(existing.getRuleCode())) {
            validateRuleCode(dto.getRuleCode());
            CodeRule conflict = baseMapper.selectByRuleCode(dto.getRuleCode());
            if (conflict != null && !conflict.getId().equals(existing.getId())) {
                throw new IllegalArgumentException("规则编码已被使用");
            }
            existing.setRuleCode(dto.getRuleCode());
        }
        if (dto.getSegments() != null) {
            validateSegments(dto.getSegments());
            existing.setSegmentsJson(serializeSegments(dto.getSegments()));
        }
        if (dto.getBatchSize() != null) {
            existing.setBatchSize(normalizeBatchSize(dto.getBatchSize()));
        } else if (existing.getBatchSize() == null) {
            existing.setBatchSize(defaultPoolBatchSize);
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        if (dto.getRuleName() != null) {
            existing.setRuleName(dto.getRuleName());
        }
        if (dto.getRemark() != null) {
            existing.setRemark(dto.getRemark());
        }
        existing.setUpdateTime(LocalDateTime.now());
        Long uid = UserContextRef.currentUserIdOrNull();
        if (uid != null) {
            existing.setUpdateId(uid);
        }
        updateById(existing);
        // best-effort：删 pool key（变更 segments / batch_size / status 后旧号段可能渲染失败）
        try {
            evictPoolKeys(existing.getRuleCode());
        } catch (RuntimeException ignore) {
            // best-effort，不抛
        }
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        CodeRule existing = getById(id);
        if (existing == null) {
            return;
        }
        Long uid = UserContextRef.currentUserIdOrNull();
        if (uid != null) {
            existing.setDeleteId(uid);
        }
        existing.setDeleteTime(LocalDateTime.now());
        // 直接改 MP 软删字段（MP @TableLogic 会基于条件 UPDATE deleted=1）
        removeById(id);
        // best-effort：禁用的规则不应再被发号
        try {
            evictPoolKeys(existing.getRuleCode());
        } catch (RuntimeException ignore) {
            // best-effort
        }
    }

    @Override
    public List<CodeSequence> listSequences(CodeSequenceQueryDTO query) {
        Long effectiveRuleId = query == null ? null : query.getRuleId();
        String ruleCode = query == null ? null : query.getRuleCode();
        if (effectiveRuleId == null && ruleCode != null && !ruleCode.isEmpty()) {
            CodeRule rule = baseMapper.selectByRuleCode(ruleCode);
            if (rule == null) {
                return Collections.emptyList();
            }
            effectiveRuleId = rule.getId();
        }
        if (effectiveRuleId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<CodeSequence> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CodeSequence::getRuleId, effectiveRuleId)
                .orderByAsc(CodeSequence::getPeriodKey);
        return codeSequenceMapper.selectList(wrapper);
    }

    /**
     * 校验 segments 列表（恰好一个 SEQUENCE、至多一个 DATE；FIXED / DATE / SEQUENCE 字段合规）。
     */
    private void validateSegments(List<CodeSegmentDTO> segments) {
        if (segments == null || segments.isEmpty()) {
            throw new IllegalArgumentException("segments 不能为空");
        }
        int seqCount = 0;
        int dateCount = 0;
        for (CodeSegmentDTO seg : segments) {
            if (seg == null) {
                throw new IllegalArgumentException("segments 含有空项");
            }
            String type = seg.getType();
            if (type == null || type.isEmpty()) {
                throw new IllegalArgumentException("片段 type 不能为空");
            }
            switch (type) {
                case "FIXED":
                    if (seg.getValue() == null) {
                        throw new IllegalArgumentException("FIXED 片段 value 不能为空");
                    }
                    break;
                case "DATE":
                    if (seg.getPattern() == null || !DATE_PATTERN_WHITELIST.contains(seg.getPattern())) {
                        throw new IllegalArgumentException("DATE.pattern 必须在白名单内");
                    }
                    dateCount++;
                    break;
                case "SEQUENCE":
                    if (seg.getLength() == null || seg.getLength() < 1 || seg.getLength() > 18) {
                        throw new IllegalArgumentException("SEQUENCE.length 必须在 1~18 之间");
                    }
                    long step = seg.getStep() == null ? 1L : seg.getStep();
                    if (step < 1) {
                        throw new IllegalArgumentException("SEQUENCE.step 必须 >= 1");
                    }
                    seqCount++;
                    break;
                default:
                    throw new IllegalArgumentException("未知片段类型: " + type);
            }
        }
        if (seqCount != 1) {
            throw new IllegalArgumentException("segments 必须恰好包含 1 个 SEQUENCE");
        }
        if (dateCount > 1) {
            throw new IllegalArgumentException("DATE 片段至多 1 个");
        }
    }

    private void validateRuleCode(String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("ruleCode 不能为空");
        }
        if (!RULE_CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("ruleCode 必须以字母开头，仅含字母数字下划线，长度 1~64");
        }
    }

    private int normalizeBatchSize(Integer raw) {
        int v = (raw == null || raw <= 0) ? defaultPoolBatchSize : raw;
        if (v < 1 || v > 5000) {
            throw new IllegalArgumentException("batchSize 必须在 1~5000 之间");
        }
        return v;
    }

    private String serializeSegments(List<CodeSegmentDTO> segments) {
        try {
            return objectMapper.writeValueAsString(segments);
        } catch (Exception e) {
            throw new RuntimeException("序列化 segments 失败", e);
        }
    }

    /**
     * 把 segments_json 反序列化为强类型列表（生成 / 预览 使用）。
     */
    public List<CodeSegmentDTO> parseSegments(String segmentsJson) {
        if (segmentsJson == null || segmentsJson.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(segmentsJson, new TypeReference<List<CodeSegmentDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("反序列化 segments 失败", e);
        }
    }

    /**
     * 清掉指定 ruleCode 下所有可能存在的 Redis pool key（按已知 periodKey 有限枚举 + 通配 SCAN 兜底）。
     */
    private void evictPoolKeys(String ruleCode) {
        if (ruleCode == null || ruleCode.isEmpty()) {
            return;
        }
        // 已知 periodKey 全集（GLOBAL + 当前是凌晨会少一些；这里以常见全量枚举；命中即删、不命中不报错）
        String[] keys = new String[] {
                "code:pool:" + ruleCode + ":GLOBAL",
                "code:pool:" + ruleCode + ":Y:2026",
                "code:pool:" + ruleCode + ":Y:2027",
                "code:pool:" + ruleCode + ":M:202601",
                "code:pool:" + ruleCode + ":M:202602",
                "code:pool:" + ruleCode + ":M:202608",
                "code:pool:" + ruleCode + ":D:20260801",
                "code:pool:" + ruleCode + ":D:20260802",
                "code:pool:" + ruleCode + ":D:20260803"
        };
        for (String k : keys) {
            try {
                stringRedisTemplate.delete(k);
            } catch (RuntimeException ignore) {
                // best-effort
            }
        }
        // 兜底：SCAN 找全部匹配（保守，最多 ~500 keys）
        try {
            org.springframework.data.redis.core.ScanOptions opts = org.springframework.data.redis.core.ScanOptions
                    .scanOptions().match("code:pool:" + ruleCode + ":*").count(200).build();
            try (org.springframework.data.redis.core.Cursor<String> cursor = stringRedisTemplate.scan(opts)) {
                int limit = 500;
                int n = 0;
                while (cursor.hasNext() && n < limit) {
                    stringRedisTemplate.delete(cursor.next());
                    n++;
                }
            } catch (Exception ignore) {
                // best-effort
            }
        } catch (RuntimeException ignore) {
            // best-effort
        }
    }

    /**
     * 静态内部 holder：避免直接依赖 spring 静态类。
     * 主会话上下文用 {@code com.fifthtech.security.UserContext}；这里反射桥接可避免循环 import 测试问题。
     */
    static final class UserContextRef {
        static Long currentUserIdOrNull() {
            try {
                return (Long) Class.forName("com.fifthtech.security.UserContext")
                        .getMethod("getCurrentUserId")
                        .invoke(null);
            } catch (Throwable t) {
                return null;
            }
        }
    }
}
