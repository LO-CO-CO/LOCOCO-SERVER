package com.lokoko.global.common.aop.lock;

import com.lokoko.global.common.annotation.DistributedLock;
import com.lokoko.global.common.aop.transaction.AopForTransaction;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonLockAspect {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.lokoko.global.common.annotation.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock lockAnno = method.getAnnotation(DistributedLock.class);

        String key = buildKey(signature.getParameterNames(), joinPoint.getArgs(), lockAnno.key());
        RLock rLock = redissonClient.getLock(key);

        boolean acquired;
        try {
            acquired = tryAcquireLock(rLock, lockAnno);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
        if (!acquired) {
            return false;
        }

        try {
            return executeWithTransaction(joinPoint);
        } finally {
            releaseLock(rLock, method, key);
        }
    }

    private String buildKey(String[] names, Object[] args, String spel) {
        return REDISSON_LOCK_PREFIX
                + CustomSpringELParser.getDynamicValue(names, args, spel);
    }

    private boolean tryAcquireLock(RLock lock, DistributedLock anno) throws InterruptedException {
        return lock.tryLock(anno.waitTime(), anno.leaseTime(), anno.timeUnit());
    }

    private Object executeWithTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        return aopForTransaction.execute(joinPoint);
    }

    private void releaseLock(RLock lock, Method method, String key) {
        try {
            lock.unlock();
        } catch (IllegalMonitorStateException e) {
            log.warn("서비스={}, 키={} 락 해제 실패", method.getName(), key);
        }
    }
}