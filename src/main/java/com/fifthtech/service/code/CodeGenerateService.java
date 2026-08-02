package com.fifthtech.service.code;

import java.time.Instant;
import java.util.List;

/**
 * CodeGenerateService
 *
 * <p>号段池取号 + 试拼（不占号）。与规则 CRUD 解耦；管理端通过
 * {@code CodeRuleService} 维护规则，运行期通过 {@code CodeGenerateService} 取号。</p>
 *
 * @author RH
 * @description 编码取号服务接口
 * @date 2026-08-02
 */
public interface CodeGenerateService {

    /**
     * 取一个号（业务时间 = 默认时区 Asia/Shanghai + 系统当前时间）。
     */
    String next(String ruleCode);

    /**
     * 取一个号，指定业务时间（{@code bizTime} ISO-8601）。
     */
    String next(String ruleCode, Instant bizTime);

    /**
     * 批量取号（同一 periodKey 内、按顺序升序）。
     *
     * @param count 已由 Controller 限幅到 [1, code.generate.max-batch-count]
     */
    List<String> nextBatch(String ruleCode, int count);

    /**
     * 取号 + 业务时间（批量）。
     */
    List<String> nextBatch(String ruleCode, int count, Instant bizTime);

    /**
     * 试拼：基于当前 DB 水位 +1 渲染一段样例，<strong>不</strong>消费号。
     */
    String preview(String ruleCode);

    /**
     * 试拼：指定业务时间。
     */
    String preview(String ruleCode, Instant bizTime);
}
