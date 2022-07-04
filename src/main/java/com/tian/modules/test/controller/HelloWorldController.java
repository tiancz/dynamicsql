package com.tian.modules.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class HelloWorldController {

    @CrossOrigin
    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        log.info("queryAllDept response:{}", "hello");
        return "hello";
    }
}
