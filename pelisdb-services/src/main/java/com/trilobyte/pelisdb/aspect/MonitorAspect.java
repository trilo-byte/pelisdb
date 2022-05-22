package com.trilobyte.pelisdb.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@Slf4j
public class MonitorAspect {

    @Pointcut("execution(public * *(..))")
    private void anyPublicOperation() { //
    }

    @Pointcut("within(@com.trilobyte.pelisdb.aspect.annotation.Monitor *)")
    private void beanAnnotatedWithMonitor() { //
    }

    @Pointcut("@annotation(com.trilobyte.pelisdb.aspect.annotation.Monitor)")
    private void methodAnnotatedWithMonitor() { //
    }

    @Around("anyPublicOperation() and (beanAnnotatedWithMonitor() or methodAnnotatedWithMonitor())")
    public Object aroundAdvice(final ProceedingJoinPoint jp) throws Throwable {
        final long timeStart = System.currentTimeMillis();
        try {
            return jp.proceed();
        } finally {
            if (log.isDebugEnabled()) {
                final long timeElapsed = System.currentTimeMillis() - timeStart;
                final String methodName = jp.getSignature().getName();
                final String className = jp.getTarget().getClass().getName();
                log.debug("{}#{}: {}ms", className, methodName, timeElapsed);
            }
        }
    }
}
