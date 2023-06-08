package com.example.demo1.controller;

import com.example.demo1.utils.IpUtils;
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
    @GetMapping
    public String test(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return "try " + IpUtils.getIpAddr(attributes.getRequest());
    }
}
