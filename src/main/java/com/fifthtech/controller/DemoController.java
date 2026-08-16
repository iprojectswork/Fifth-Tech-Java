package com.fifthtech.controller;

import com.fifthtech.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RH
 * @ClassName DemoController
 * @description: 示例控制器
 * @date 2026年08月16日
 * @version: 1.0
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    /**
    * @description: 示例接口
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link Result}<{@link String}>
    **/
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("hello world");
    }
}