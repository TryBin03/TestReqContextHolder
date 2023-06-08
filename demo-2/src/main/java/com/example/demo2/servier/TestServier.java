package com.example.demo2.servier;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author TryBin
 */
@Component //注册为spring组件，交予IOC容器管理
@FeignClient(value = "demo-1") //添加FeignClient注解，绑定服务提供者。
public interface TestServier {
    @GetMapping("/test")
    String test();

}
