package com.fifthtech.service.code.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifthtech.common.BizConstants;
import com.fifthtech.dao.entity.code.CodeRule;
import com.fifthtech.dto.code.CodeSegmentDTO;
import com.fifthtech.service.code.CodeGenerateService;
import com.fifthtech.service.code.CodeRuleService;
import com.fifthtech.service.code.CodeSequenceService;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author RH
 * @ClassName CodeGenerateServiceImpl
 * @description: 编码取号服务实现
 * @date 2026年08月02日
 * @version: 1.0
 */
@Service
public class CodeGenerateServiceImpl implements CodeGenerateService {

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

    private static final String PERIOD_GLOBAL = "GLOBAL";
    private static final DateTimeFormatter FORMATTER_YYYY = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter FORMATTER_YYYYMM = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Resource
    private CodeRuleService codeRuleService;

    @Resource
    private CodeSequenceService codeSequenceService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Value("${code.generate.lock-wait-seconds:5}")
    private long lockWaitSeconds;

    @Value("${code.generate.lock-lease-seconds:30}")
    private long lockLeaseSeconds;

    @Value("${code.timezone:Asia/Shanghai}")
    private String timezone;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String next(String ruleCode) {
        return next(ruleCode, null);
    }

    @Override
    public String next(String ruleCode, Instant bizTime) {
        CodeRuleLoaded loaded = loadAndValidate(ruleCode);
        LocalDateTime ldt = toLocalDateTime(bizTime);
        long serial = takeOneSerial(loaded.rule, loaded.segments, ldt);
        return render(loaded.segments, ldt, serial);
    }

    @Override
    public List<String> nextBatch(String ruleCode, int count) {
        return nextBatch(ruleCode, count, null);
    }

    @Override
    public List<String> nextBatch(String ruleCode, int count, Instant bizTime) {
        if (count <= 0) {
            return Collections.emptyList();
        }
        CodeRuleLoaded loaded = loadAndValidate(ruleCode);
        LocalDateTime ldt = toLocalDateTime(bizTime);
        List<Long> serials = takeBatchSerial(loaded.rule, loaded.segments, ldt, count);
        if (serials.size() != count) {
            throw new RuntimeException("批量取号未凑满: expected=" + count + ", actual=" + serials.size());
        }
        List<String> out = new ArrayList<>(serials.size());
        for (Long serial : serials) {
            out.add(render(loaded.segments, ldt, serial));
        }
        return out;
    }

    @Override
    public String preview(String ruleCode) {
        return preview(ruleCode, null);
    }

    @Override
    public String preview(String ruleCode, Instant bizTime) {
        CodeRuleLoaded loaded = loadAndValidate(ruleCode);
        LocalDateTime ldt = toLocalDateTime(bizTime);
        DateSegment ds = findDateSegment(loaded.segments);
        long serial = readCurrentMaxPlusOne(loaded.rule, loaded.segments, ds, ldt);
        return render(loaded.segments, ldt, serial);
    }

    private CodeRuleLoaded loadAndValidate(String ruleCode) {
        if (ruleCode == null || ruleCode.isEmpty()) {
            throw new IllegalArgumentException("ruleCode 不能为空");
        }
        CodeRule rule = codeRuleService.infoByRuleCode(ruleCode);
        if (rule == null) {
            throw new IllegalArgumentException("规则不存在或已删除: " + ruleCode);
        }
        if (rule.getStatus() == null || rule.getStatus() != BizConstants.STATUS_ENABLED) {
            throw new IllegalArgumentException("规则已禁用");
        }
        List<CodeSegmentDTO> segments = parseSegments(rule.getSegmentsJson());
        validateSegmentsOrThrow(segments);
        return new CodeRuleLoaded(rule, segments);
    }

    private List<CodeSegmentDTO> parseSegments(String json) {
        if (json == null || json.isEmpty()) {
            throw new IllegalArgumentException("规则 segments 为空");
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<CodeSegmentDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("反序列化 segments 失败", e);
        }
    }

    private static class CodeRuleLoaded {
        final CodeRule rule;
        final List<CodeSegmentDTO> segments;
        CodeRuleLoaded(CodeRule rule, List<CodeSegmentDTO> segments) {
            this.rule = rule;
            this.segments = segments;
        }
    }

    private void validateSegmentsOrThrow(List<CodeSegmentDTO> segments) {
        int seqCount = 0;
        int dateCount = 0;
        for (CodeSegmentDTO seg : segments) {
            if (seg == null || seg.getType() == null) {
                throw new IllegalArgumentException("片段非法");
            }
            switch (seg.getType()) {
                case "FIXED":
                    break;
                case "DATE":
                    if (seg.getPattern() == null || !DATE_PATTERN_WHITELIST.contains(seg.getPattern())) {
                        throw new IllegalArgumentException("DATE.pattern 非法");
                    }
                    dateCount++;
                    break;
                case "SEQUENCE":
                    if (seg.getLength() == null || seg.getLength() < 1) {
                        throw new IllegalArgumentException("SEQUENCE.length 非法");
                    }
                    seqCount++;
                    break;
                default:
                    throw new IllegalArgumentException("未知片段类型: " + seg.getType());
            }
        }
        if (seqCount != 1) {
            throw new IllegalArgumentException("必须恰好 1 个 SEQUENCE");
        }
        if (dateCount > 1) {
            throw new IllegalArgumentException("DATE 至多 1 个");
        }
    }

    private LocalDateTime toLocalDateTime(Instant bizTime) {
        Instant actual = bizTime != null ? bizTime : Instant.now();
        return LocalDateTime.ofInstant(actual, ZoneId.of(timezone));
    }

    private static class DateSegment {
        final String pattern;
        final String periodKey;
        final String granularity;
        DateSegment(String pattern, String periodKey, String granularity) {
            this.pattern = pattern;
            this.periodKey = periodKey;
            this.granularity = granularity;
        }
    }

    private DateSegment findDateSegment(List<CodeSegmentDTO> segments) {
        for (CodeSegmentDTO seg : segments) {
            if ("DATE".equals(seg.getType())) {
                return buildDateSegment(seg.getPattern());
            }
        }
        return new DateSegment(null, PERIOD_GLOBAL, "GLOBAL");
    }

    private DateSegment buildDateSegment(String pattern) {
        if (!DATE_PATTERN_WHITELIST.contains(pattern)) {
            throw new IllegalArgumentException("DATE.pattern 非白名单: " + pattern);
        }
        return switch (pattern) {
            case "yy", "yyyy" -> new DateSegment(pattern, "Y:", "Y");
            case "yyMM", "yyyyMM" -> new DateSegment(pattern, "M:", "M");
            case "yyMMdd", "yyyyMMdd" -> new DateSegment(pattern, "D:", "D");
            default -> new DateSegment(pattern, PERIOD_GLOBAL, "GLOBAL");
        };
    }

    private String resolvePeriodKey(DateSegment ds, LocalDateTime ldt) {
        if (ds == null || PERIOD_GLOBAL.equals(ds.granularity)) {
            return PERIOD_GLOBAL;
        }
        String suffix = switch (ds.granularity) {
            case "Y" -> ldt.format(FORMATTER_YYYY);
            case "M" -> ldt.format(FORMATTER_YYYYMM);
            case "D" -> ldt.format(FORMATTER_YYYYMMDD);
            default -> PERIOD_GLOBAL;
        };
        return ds.periodKey + suffix;
    }

    private String poolKey(String ruleCode, String periodKey) {
        return "code:pool:" + ruleCode + ":" + periodKey;
    }

    private String lockKey(String ruleCode, String periodKey) {
        return "code:lock:" + ruleCode + ":" + periodKey;
    }

    private long takeOneSerial(CodeRule rule, List<CodeSegmentDTO> segments, LocalDateTime ldt) {
        CodeSegmentDTO seq = findSequenceSegment(segments);
        DateSegment ds = findDateSegment(segments);
        String periodKey = resolvePeriodKey(ds, ldt);
        String pool = poolKey(rule.getRuleCode(), periodKey);

        Long fromPool = popSerial(pool);
        if (fromPool != null) {
            return fromPool;
        }

        RLock lock = redissonClient.getLock(lockKey(rule.getRuleCode(), periodKey));
        boolean acquired = false;
        try {
            acquired = lock.tryLock(lockWaitSeconds, lockLeaseSeconds, TimeUnit.SECONDS);
            if (!acquired) {
                throw new RuntimeException("获取号段锁超时");
            }
            Long again = popSerial(pool);
            if (again != null) {
                return again;
            }
            List<Long> batch = allocateBatchViaService(rule, seq, periodKey);
            if (batch.isEmpty()) {
                throw new RuntimeException("取号失败：预支号段为空");
            }
            pushBatchToPool(pool, batch, ds.granularity);
            Long serial = popSerial(pool);
            if (serial == null) {
                throw new RuntimeException("补段后仍无法取号");
            }
            return serial;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("取号被中断");
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private List<Long> takeBatchSerial(CodeRule rule, List<CodeSegmentDTO> segments, LocalDateTime ldt, int count) {
        CodeSegmentDTO seq = findSequenceSegment(segments);
        DateSegment ds = findDateSegment(segments);
        String periodKey = resolvePeriodKey(ds, ldt);
        String pool = poolKey(rule.getRuleCode(), periodKey);

        List<Long> out = new ArrayList<>(count);
        while (out.size() < count) {
            Long popped = popSerial(pool);
            if (popped != null) {
                out.add(popped);
                continue;
            }
            RLock lock = redissonClient.getLock(lockKey(rule.getRuleCode(), periodKey));
            boolean acquired = false;
            try {
                acquired = lock.tryLock(lockWaitSeconds, lockLeaseSeconds, TimeUnit.SECONDS);
                if (!acquired) {
                    throw new RuntimeException("获取号段锁超时");
                }
                while (out.size() < count) {
                    Long again = popSerial(pool);
                    if (again != null) {
                        out.add(again);
                        continue;
                    }
                    break;
                }
                if (out.size() >= count) {
                    break;
                }
                List<Long> batch = allocateBatchViaService(rule, seq, periodKey);
                if (batch.isEmpty()) {
                    throw new RuntimeException("取号失败：预支号段为空");
                }
                pushBatchToPool(pool, batch, ds.granularity);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("取号被中断");
            } finally {
                if (acquired && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        Collections.sort(out);
        return out;
    }

    private Long popSerial(String pool) {
        String popped = stringRedisTemplate.opsForList().leftPop(pool);
        if (popped == null) {
            return null;
        }
        try {
            return Long.parseLong(popped);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Redis 池中值非数字: " + popped);
        }
    }

    private void pushBatchToPool(String pool, List<Long> batch, String granularity) {
        if (batch.isEmpty()) {
            return;
        }
        String[] arr = new String[batch.size()];
        for (int i = 0; i < batch.size(); i++) {
            arr[i] = String.valueOf(batch.get(i));
        }
        stringRedisTemplate.opsForList().rightPushAll(pool, arr);
        Long ttlSeconds = ttlSecondsOfGranularity(granularity);
        if (ttlSeconds != null) {
            stringRedisTemplate.expire(pool, ttlSeconds, TimeUnit.SECONDS);
        }
    }

    private Long ttlSecondsOfGranularity(String granularity) {
        if (granularity == null) {
            return null;
        }
        return switch (granularity) {
            case "D" -> 3L * 24 * 3600;
            case "M" -> 40L * 24 * 3600;
            case "Y", "GLOBAL" -> null;
            default -> null;
        };
    }

    private List<Long> allocateBatchViaService(CodeRule rule, CodeSegmentDTO seq, String periodKey) {
        return codeSequenceService.allocateBatch(rule, seq, periodKey);
    }

    private long readCurrentMaxPlusOne(CodeRule rule, List<CodeSegmentDTO> segments, DateSegment ds, LocalDateTime ldt) {
        String periodKey = resolvePeriodKey(ds, ldt);
        Long max = codeSequenceService.findCurrentMax(rule.getId(), periodKey);
        if (max != null) {
            return max + 1L;
        }
        CodeSegmentDTO seq = findSequenceSegment(segments);
        return seq.getStart() == null ? 1L : seq.getStart();
    }

    private CodeSegmentDTO findSequenceSegment(List<CodeSegmentDTO> segments) {
        for (CodeSegmentDTO seg : segments) {
            if ("SEQUENCE".equals(seg.getType())) {
                return seg;
            }
        }
        throw new RuntimeException("缺少 SEQUENCE 片段");
    }

    private String render(List<CodeSegmentDTO> segments, LocalDateTime ldt, long serial) {
        StringBuilder sb = new StringBuilder();
        for (CodeSegmentDTO seg : segments) {
            switch (seg.getType()) {
                case "FIXED":
                    sb.append(seg.getValue());
                    break;
                case "DATE":
                    sb.append(ldt.format(DateTimeFormatter.ofPattern(seg.getPattern())));
                    break;
                case "SEQUENCE":
                    sb.append(leftPad(String.valueOf(serial), seg.getLength() == null ? 1 : seg.getLength(), '0'));
                    break;
                default:
                    break;
            }
        }
        return sb.toString();
    }

    private static String leftPad(String value, int width, char padChar) {
        if (width < 1 || value.length() >= width) {
            return value;
        }
        StringBuilder padded = new StringBuilder(width);
        for (int i = 0; i < width - value.length(); i++) {
            padded.append(padChar);
        }
        padded.append(value);
        return padded.toString();
    }
}