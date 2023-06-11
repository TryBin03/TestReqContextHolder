package com.example.api.factory;

import com.example.api.RemoteTestService;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteTestFallbackFactory implements FallbackFactory<RemoteTestService> {
    @Override
    public RemoteTestService create(Throwable cause) {
        return new RemoteTestService() {
            @Override
            public String test() {
                return null;
            }
        };
    }
}
