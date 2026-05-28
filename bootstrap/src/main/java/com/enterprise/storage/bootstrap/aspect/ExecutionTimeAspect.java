package com.enterprise.storage.bootstrap.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {

    private static final Logger log = LoggerFactory.getLogger(ExecutionTimeAspect.class);

    // This "Pointcut" tells Spring to intercept ANY method carrying our custom annotation
    @Around("@annotation(com.enterprise.storage.domain.annotations.LogExecutionTime)")
    public Object profileExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        // Let the actual core Domain method execute...
        Object proceed = joinPoint.proceed();
        
        long executionTime = System.currentTimeMillis() - start;
        
        log.info("[PERFORMANCE PROFILER] {} executed in {} ms", joinPoint.getSignature().toShortString(), executionTime);
        
        return proceed;
    }
}
