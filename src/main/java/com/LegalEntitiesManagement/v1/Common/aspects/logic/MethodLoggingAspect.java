package com.LegalEntitiesManagement.v1.Common.aspects.logic;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Aspect
@Component
public class MethodLoggingAspect {
    @Pointcut("within(com.LegalEntitiesManagement.v1.Entities.controllers..*)")
    public void controllerLayer() {}

    @Pointcut("within(com.LegalEntitiesManagement.v1.Entities.services..*)")
    public void serviceLayer() {}

    @Pointcut("within(com.LegalEntitiesManagement.v1.Entities.repositories..*)")
    public void repositoryLayer() {}

    @Pointcut("controllerLayer() || serviceLayer() || repositoryLayer()")
    public void applicationLayer() {}

    @Around("applicationLayer()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get logger for the specific class
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

        // Get method signature
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        // Create identifier for the method call
        final String methodIdentifier = className + "." + methodName;

        // Get method parameters
        String[] paramNames = methodSignature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();

        // Start stopwatch
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            // Log method entry
            logger.info("Entering method: {} with parameters: {}",
                    methodIdentifier,
                    formatParameters(paramNames, paramValues));

            // Execute method
            Object result = joinPoint.proceed();

            // Stop timing
            stopWatch.stop();

            // Log method exit
            logger.info("Exiting method: {} - execution time: {} ms",
                    methodIdentifier,
                    stopWatch.getTotalTimeMillis());

            if (result != null && logger.isDebugEnabled()) {
                logger.debug("Method {} returned: {}", methodIdentifier, result);
            }

            return result;

        } catch (Throwable e) {
            // Log error
            logger.error("Exception in {}: {} - {}",
                    methodIdentifier,
                    e.getClass().getSimpleName(),
                    e.getMessage());

            // Stop timing
            stopWatch.stop();

            // Log execution time even in case of error
            logger.info("Method {} failed - execution time: {} ms",
                    methodIdentifier,
                    stopWatch.getTotalTimeMillis());

            throw e;
        }
    }

    private String formatParameters(String[] paramNames, Object[] paramValues) {
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < paramNames.length; i++) {
            if (i > 0) {
                params.append(", ");
            }
            params.append(paramNames[i]).append("=");

            // Handle array parameters
            if (paramValues[i] != null && paramValues[i].getClass().isArray()) {
                params.append(Arrays.toString((Object[]) paramValues[i]));
            } else {
                params.append(paramValues[i]);
            }
        }
        return params.toString();
    }
}
