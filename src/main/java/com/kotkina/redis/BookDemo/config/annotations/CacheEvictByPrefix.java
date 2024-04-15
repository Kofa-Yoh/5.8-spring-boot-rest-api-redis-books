package com.kotkina.redis.BookDemo.config.annotations;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvictByPrefix {

    String cacheName();

    String key();

    String prefixEnd() default "";
}
