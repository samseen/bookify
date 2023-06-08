package com.samseen.bookify.core.limit;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    long count() default 10;

    Type type() default Type.IP;

    long period() default 1;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    enum Type {
        IP, TOKEN
    }
}
