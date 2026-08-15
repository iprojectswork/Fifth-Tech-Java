package com.fifthtech.controller.code;

import com.fifthtech.common.Result;
import com.fifthtech.common.utils.ConvertUtils;
import com.fifthtech.dao.entity.code.CodeSequence;
import com.fifthtech.dto.code.CodeSequenceQueryDTO;
import com.fifthtech.service.code.CodeRuleService;
import com.fifthtech.vo.code.CodeSequenceVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CodeSequenceController
 *
 * @description 编码流水水位查询（只读）
 * @date 2026-08-02
 */
@RestController
@RequestMapping("/code/sequence")
public class CodeSequenceController {

    @Resource
    private CodeRuleService codeRuleService;

    /**
     * 流水水位列表
     */
    @GetMapping("/list")
    public Result<List<CodeSequenceVO>> list(CodeSequenceQueryDTO query) {
        List<CodeSequence> rows = codeRuleService.listSequences(query);
        List<CodeSequenceVO> vos = ConvertUtils.toVOList(rows, CodeSequenceVO.class);
        return Result.success("查询成功", vos);
    }
}
