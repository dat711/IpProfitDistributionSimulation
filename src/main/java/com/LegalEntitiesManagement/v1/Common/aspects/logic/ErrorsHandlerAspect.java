package com.LegalEntitiesManagement.v1.Common.aspects.logic;

import com.LegalEntitiesManagement.v1.Common.aspects.helpers.ResponseHeadersHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public class ErrorsHandlerAspect {
    private ResponseEntity<ProblemDetail> notFoundProblemDetail(RuntimeException e){
        boolean notFound = e.getMessage().contains("Cannot find");
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        String[] classNamePath = e.getClass().getName().split("\\.");
        String className = classNamePath[classNamePath.length -1];
        problemDetail.setTitle(String.format("The Error: %s occur" ,className));

        return notFound ? ResponseEntity.status(HttpStatus.NOT_FOUND).headers(ResponseHeadersHelper.getBaseHeaders()).body(problemDetail)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(ResponseHeadersHelper.getBaseHeaders()).body(problemDetail);
    }

    @Around("@annotation(com.LegalEntitiesManagement.v1.Common.aspects.annotations.AspectErrorsHandler)")
    public Object HandleException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch(Throwable throwable){
            assert throwable instanceof RuntimeException;
            RuntimeException e = (RuntimeException) throwable;
            return this.notFoundProblemDetail(e);
        }
    }
}
