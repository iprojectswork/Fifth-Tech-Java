package com.fifthtech.controller.code;

import com.fifthtech.common.Result;
import com.fifthtech.dto.code.CodeGenerateRequestDTO;
import com.fifthtech.dto.code.CodePreviewRequestDTO;
import com.fifthtech.service.code.CodeGenerateService;
import com.fifthtech.vo.code.CodeGenerateResponseVO;
import com.fifthtech.vo.code.CodePreviewResponseVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

/**
 * @author RH
 * @ClassName CodeGenerateController
 * @description: 编码取号控制器
 * @date 2026年08月02日
 * @version: 1.0
 */
@RestController
@RequestMapping("/code")
public class CodeGenerateController {

    @Resource
    private CodeGenerateService codeGenerateService;

    @Value("${code.generate.max-batch-count:100}")
    private int maxBatchCount;

    /**
    * @description: 生成编码
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link CodeGenerateResponseVO}>
    **/
    @PostMapping("/generate")
    public Result<CodeGenerateResponseVO> generate(@RequestBody CodeGenerateRequestDTO dto) {
        if (dto == null || dto.getRuleCode() == null || dto.getRuleCode().isEmpty()) {
            return Result.error("ruleCode 不能为空");
        }
        int count = dto.getCount() == null || dto.getCount() <= 0 ? 1 : dto.getCount();
        if (count > maxBatchCount) {
            return Result.error("count 超出上限 " + maxBatchCount);
        }
        Instant bizTime;
        try {
            bizTime = parseBizTime(dto.getBizTime());
        } catch (DateTimeParseException dpe) {
            return Result.error("bizTime 格式非法（须 ISO-8601）");
        } catch (RuntimeException ex) {
            return Result.error(ex.getMessage());
        }
        try {
            List<String> codes = count == 1
                    ? Collections.singletonList(codeGenerateService.next(dto.getRuleCode(), bizTime))
                    : codeGenerateService.nextBatch(dto.getRuleCode(), count, bizTime);
            CodeGenerateResponseVO vo = new CodeGenerateResponseVO();
            vo.setRuleCode(dto.getRuleCode());
            vo.setCodes(codes);
            return Result.success("生成成功", vo);
        } catch (RuntimeException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 预览编码
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link Result}<{@link CodePreviewResponseVO}>
    **/
    @PostMapping("/preview")
    public Result<CodePreviewResponseVO> preview(@RequestBody CodePreviewRequestDTO dto) {
        if (dto == null || dto.getRuleCode() == null || dto.getRuleCode().isEmpty()) {
            return Result.error("ruleCode 不能为空");
        }
        Instant bizTime;
        try {
            bizTime = parseBizTime(dto.getBizTime());
        } catch (DateTimeParseException dpe) {
            return Result.error("bizTime 格式非法（须 ISO-8601）");
        } catch (RuntimeException ex) {
            return Result.error(ex.getMessage());
        }
        try {
            String sample = codeGenerateService.preview(dto.getRuleCode(), bizTime);
            CodePreviewResponseVO vo = new CodePreviewResponseVO();
            vo.setRuleCode(dto.getRuleCode());
            vo.setSample(sample);
            return Result.success("查询成功", vo);
        } catch (RuntimeException ex) {
            return Result.error(ex.getMessage());
        }
    }

    /**
    * @description: 解析业务时间
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [bizTime]
    * @return: {@link Instant}
    **/
    private static Instant parseBizTime(String bizTime) {
        if (bizTime == null || bizTime.isEmpty()) {
            return null;
        }
        return Instant.parse(bizTime);
    }
}