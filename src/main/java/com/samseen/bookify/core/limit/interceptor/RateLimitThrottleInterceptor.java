package com.samseen.bookify.core.limit.interceptor;

import com.samseen.bookify.core.JsonUtils;
import com.samseen.bookify.core.limit.RateLimit;
import com.samseen.bookify.core.response.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
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

            String limitRate = String.join("/", limiter.getConfig().getRate().toString(),
                    limiter.getConfig().getRateInterval().toString());
            response.setHeader("X-Rate-Limit-Limt", limitRate);
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(limiter.availablePermits()));
            return isAcquired;
        }

        return true;
    }

    public String getRateKey(RateLimit.Type type, HttpServletRequest request) {
        switch (type) {
            case IP:
                return getIp(request);
            default:
                return getToken(request);
        }
    }

    public String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }

        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip)) {
            return ip;
        }
        return request.getRemoteHost();
    }

    public String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("x-api-key");
        if (StringUtils.isNotEmpty(authorization)) {
            return authorization.toUpperCase();
        }
        return getIp(request);
    }

    public RateIntervalUnit toTimeUnit(TimeUnit timeUnit) {
        switch (timeUnit) {
            case MINUTES:
                return RateIntervalUnit.MINUTES;
            case SECONDS:
                return RateIntervalUnit.SECONDS;
            case DAYS:
                return RateIntervalUnit.DAYS;
            case HOURS:
                return RateIntervalUnit.HOURS;
            default:
                return RateIntervalUnit.MILLISECONDS;
        }
    }
}
