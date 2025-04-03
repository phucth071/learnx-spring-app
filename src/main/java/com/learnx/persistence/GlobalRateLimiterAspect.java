package com.learnx.persistence;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

//@Aspect
//@Component
public class GlobalRateLimiterAspect {

    private final RateLimiter rateLimiter;

    public GlobalRateLimiterAspect(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiter = rateLimiterRegistry.rateLimiter("default");
    }

    @Before("execution(* com.hcmute.utezbe..*(..))")
    public void rateLimit() {
        rateLimiter.acquirePermission();
    }
}