package com.example.api;

import com.example.api.factory.RemoteTestFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author trybin
 */
@FeignClient(contextId = "remoteTestService", value = "demo1", fallbackFactory = RemoteTestFallbackFactory.class) //添加FeignClient注解，绑定服务提供者。
public interface RemoteTestService {
    @GetMapping("/test/t")
    String test();
}
