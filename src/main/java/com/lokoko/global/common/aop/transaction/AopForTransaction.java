package com.lokoko.global.common.aop.transaction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AopForTransaction {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
