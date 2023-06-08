package com.example.demo2.controller;

import com.example.demo2.servier.TestServier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TryBin
 */
@RestController
@RequestMapping({"test"})
public class TestController {

    @Autowired
    TestServier testServier;
    @GetMapping
    public String test(){
        return "demo-2 \n demo-1 ip" + testServier.test();
    }
}
