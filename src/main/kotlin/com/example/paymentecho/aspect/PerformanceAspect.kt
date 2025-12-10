package com.example.paymentecho.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class PerformanceAspect {

    private val logger = LoggerFactory.getLogger(PerformanceAspect::class.java)

    @Around("execution(* com.example.paymentecho.service.*.*(..))")
    fun measureExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
        val startTime = System.currentTimeMillis()
        val className = joinPoint.signature.declaringTypeName
        val methodName = joinPoint.signature.name

        return try {
            val result = joinPoint.proceed()
            val executionTime = System.currentTimeMillis() - startTime
            logger.debug("Method {}.{} executed in {} ms", className, methodName, executionTime)
            
            if (executionTime > 1000) {
                logger.warn("Slow method detected: {}.{} took {} ms", className, methodName, executionTime)
            }
            
            result
        } catch (e: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            logger.error("Method {}.{} failed after {} ms", className, methodName, executionTime, e)
            throw e
        }
    }
}
