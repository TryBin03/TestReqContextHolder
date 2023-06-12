package com.example.demo1.servier.impl;

import com.example.common.utils.IpUtils;
import com.example.common.utils.ServletUtils;
import com.example.demo1.servier.TestServier;
import org.springframework.stereotype.Service;

/**
 * @author trybin
 */
@Service("testServier")
public class TsetServierImpl implements TestServier {
    public String test(){
        return "try " + IpUtils.getIpAddr(ServletUtils.getRequest());
    }

}
