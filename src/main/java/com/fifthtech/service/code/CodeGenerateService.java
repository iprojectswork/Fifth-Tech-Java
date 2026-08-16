package com.fifthtech.service.code;

import java.time.Instant;
import java.util.List;

/**
 * @author RH
 * @ClassName CodeGenerateService
 * @description: 编码取号服务接口
 * @date 2026年08月02日
 * @version: 1.0
 */
public interface CodeGenerateService {

    /**
    * @description: 取一个号，业务时间取系统当前时间（默认时区 Asia/Shanghai）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ruleCode]
    * @return: {@link String}
    **/
    String next(String ruleCode);

    /**
    * @description: 取一个号，业务时间由调用方指定（ISO-8601 Instant）
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ruleCode, bizTime]
    * @return: {@link String}
    **/
    String next(String ruleCode, Instant bizTime);

    /**
    * @description: 批量取号，同一 periodKey 内按顺序升序；count 由 Controller 限幅
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ruleCode, count]
    * @return: {@link List}<{@link String}>
    **/
    List<String> nextBatch(String ruleCode, int count);

    /**
    * @description: 批量取号，业务时间由调用方指定
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ruleCode, count, bizTime]
    * @return: {@link List}<{@link String}>
    **/
    List<String> nextBatch(String ruleCode, int count, Instant bizTime);

    /**
    * @description: 试拼样例，基于当前 DB 水位 +1 渲染一段示例，不消费号
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ruleCode]
    * @return: {@link String}
    **/
    String preview(String ruleCode);

    /**
    * @description: 试拼样例，业务时间由调用方指定
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [ruleCode, bizTime]
    * @return: {@link String}
    **/
    String preview(String ruleCode, Instant bizTime);
}
