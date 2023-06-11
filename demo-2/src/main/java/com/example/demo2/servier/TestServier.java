package com.example.demo2.servier;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author TryBin
 */
public interface TestServier {
    String test();

}
