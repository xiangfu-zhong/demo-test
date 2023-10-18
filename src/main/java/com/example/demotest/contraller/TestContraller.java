package com.example.demotest.contraller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestContraller {

    static  char c;

    @GetMapping("/test")
    public String test(){
        return "response=====v1.0.0";
    }
}
