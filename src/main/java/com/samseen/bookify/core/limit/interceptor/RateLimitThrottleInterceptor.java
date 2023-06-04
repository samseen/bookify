package com.samseen.bookify.core.limit.interceptor;

import org.flywaydb.core.internal.util.JsonUtils;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class RateLimitThrottleInterceptor implements HandlerInterceptor {

    private final RedissonClient redissonClient;

    public RateLimitThrottleInterceptor(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        Method method = ((HandlerMethod) handler).getMethod();
        if (method.isAnnotationPresent(RateLimit.class)) {
            RateLimit rateLimit = method.getAnnotation(RateLimit.class);
            RRateLimiter limiter = redissonClient.getRateLimiter(getRateKey(rateLimit.type(), request));
            limiter.trySetRate(RateType.OVERALL, rateLimit.count(), rateLimit.period(),
                    toTimeUnit(rateLimit.timeUnit()));
            boolean isAcquired = limiter.tryAcquire();
            if (!isAcquired) {
                String message = "Too many request";
                String status = "429";
                response.setContentType("application/json");
                response.setStatus(Integer.parseInt(status));
                response.getWriter().write(JsonUtils.toJson(new ErrorResult(
                        LocalDateTime.now().toString(), status, message, message, request.getServletPath()
                )));
            }
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
