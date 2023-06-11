package com.example.demo2.servier.impl;

import com.example.api.RemoteTestService;
import com.example.demo2.servier.TestServier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author trybin
 */
@Service("testServier")
public class TsetServierImpl implements TestServier {
    @Autowired
    RemoteTestService remoteTestService;

    public String test(){
        return "demo-2 <br/> demo-1 ip " + remoteTestService.test();
    }

}
