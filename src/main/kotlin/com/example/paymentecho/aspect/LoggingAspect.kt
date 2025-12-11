package com.example.paymentecho.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect {

    private val logger = LoggerFactory.getLogger(LoggingAspect::class.java)

    @Around("execution(* com.example.paymentecho.controller.*.*(..))")
    fun logControllerMethods(joinPoint: ProceedingJoinPoint): Any? {
        val className = joinPoint.signature.declaringTypeName
        val methodName = joinPoint.signature.name
        val args = joinPoint.args

        logger.info("Entering method: {}.{} with args: {}", className, methodName, args)

        return try {
            val result = joinPoint.proceed()
            logger.info("Exiting method: {}.{} with result: {}", className, methodName, result)
            result
        } catch (e: Exception) {
            logger.error("Exception in method: {}.{}", className, methodName, e)
            throw e
        }
    }

    @Around("execution(* com.example.paymentecho.service.*.*(..))")
    fun logServiceMethods(joinPoint: ProceedingJoinPoint): Any? {
        val className = joinPoint.signature.declaringTypeName
        val methodName = joinPoint.signature.name

        logger.debug("Entering service method: {}.{}", className, methodName)

        return try {
            val result = joinPoint.proceed()
            logger.debug("Exiting service method: {}.{}", className, methodName)
            result
        } catch (e: Exception) {
            logger.error("Exception in service method: {}.{}", className, methodName, e)
            throw e
        }
    }
}
