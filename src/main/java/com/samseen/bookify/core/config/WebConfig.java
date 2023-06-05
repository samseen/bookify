package com.samseen.bookify.core.config;

import com.samseen.bookify.core.limit.interceptor.RateLimitThrottleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitThrottleInterceptor throttleInterceptor;

    public WebConfig(RateLimitThrottleInterceptor throttleInterceptor) {
        this.throttleInterceptor = throttleInterceptor;
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(throttleInterceptor);
    }
}
