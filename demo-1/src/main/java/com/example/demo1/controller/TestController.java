package com.example.demo1.controller;

import com.example.demo1.utils.IpUtils;
import com.example.demo1.utils.ServletUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author TryBin
 */
@RestController
@RequestMapping({"test"})
public class TestController {
    @GetMapping("/t")
    public String test(){
        return "try " + IpUtils.getIpAddr(ServletUtils.getRequest());
    }
}
