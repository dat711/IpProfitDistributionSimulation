package com.LegalEntitiesManagement.v1.Common.aspects.logic;

import com.LegalEntitiesManagement.v1.Common.aspects.helpers.ResponseHeadersHelper;
import jakarta.annotation.PostConstruct;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ErrorsHandlerAspect {

    private ResponseEntity<ProblemDetail> notFoundProblemDetail(RuntimeException e){
        boolean notFound = e.getMessage().contains("Can not find");
        ProblemDetail problemDetail = notFound ? ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage())
                : ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        String[] classNamePath = e.getClass().getName().split("\\.");
        String className = classNamePath[classNamePath.length -1];
        problemDetail.setTitle(String.format("The Error: %s occur" ,className));

        return notFound ? ResponseEntity.status(HttpStatus.NOT_FOUND).headers(ResponseHeadersHelper.getBaseHeaders())
                .body(problemDetail) : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .headers(ResponseHeadersHelper.getBaseHeaders()).body(problemDetail);
    }

    @Around("@annotation(com.LegalEntitiesManagement.v1.Common.aspects.annotations.AspectErrorsHandler)")
    public Object HandleException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch(Throwable throwable){
            assert throwable instanceof RuntimeException;
            System.out.println(throwable.getMessage());
            RuntimeException e = (RuntimeException) throwable;
            return e instanceof IllegalCallerException ? this.notSupportedMethodProblemDetail( (IllegalCallerException) e)
                    : this.notFoundProblemDetail(e);
        }
    }

    private ResponseEntity<ProblemDetail> notSupportedMethodProblemDetail(IllegalCallerException e){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage());
        problemDetail.setTitle("The request is try to performed un-allowed modification");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(ResponseHeadersHelper.getBaseHeaders())
                .body(problemDetail);
    }
}
