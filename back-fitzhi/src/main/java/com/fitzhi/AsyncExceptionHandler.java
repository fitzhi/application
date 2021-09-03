package com.fitzhi;

import java.lang.reflect.Method;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {

        log.error("error", throwable);
        log.error(String.format("Method name - %s", method.getName()));
        for (Object param : obj) {
            log.error(String.format("Parameter value - %s", param));
        }
    }
}