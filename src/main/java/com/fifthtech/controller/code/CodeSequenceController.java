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
 * @author RH
 * @ClassName CodeSequenceController
 * @description: 编码流水控制器
 * @date 2026年08月02日
 * @version: 1.0
 */
@RestController
@RequestMapping("/code/sequence")
public class CodeSequenceController {

    @Resource
    private CodeRuleService codeRuleService;

    /**
    * @description: 查询编码流水列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link Result}<{@link List}<{@link CodeSequenceVO}>>
    **/
    @GetMapping("/list")
    public Result<List<CodeSequenceVO>> list(CodeSequenceQueryDTO query) {
        List<CodeSequence> rows = codeRuleService.listSequences(query);
        List<CodeSequenceVO> vos = ConvertUtils.toVOList(rows, CodeSequenceVO.class);
        return Result.success("查询成功", vos);
    }
}