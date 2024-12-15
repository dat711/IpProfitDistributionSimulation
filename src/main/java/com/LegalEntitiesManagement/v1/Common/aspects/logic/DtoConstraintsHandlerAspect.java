package com.LegalEntitiesManagement.v1.Common.aspects.logic;

import com.LegalEntitiesManagement.v1.Common.aspects.helpers.ResponseHeadersHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.net.URI;
import java.util.*;

@Aspect
@Component
public class DtoConstraintsHandlerAspect {
    @Around("@annotation(com.LegalEntitiesManagement.v1.Common.aspects.annotations.CheckRequestBody)")
    public Object handleValidationsResult(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        BindingResult bindingResult = (BindingResult) args[args.length - 1];
        if (!bindingResult.hasErrors()){
            return joinPoint.proceed();
        }

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation errors in your request");
        problemDetail.setType(URI.create("https://errors.example.com/invalid-input"));
        problemDetail.setProperties(mapErrors(bindingResult));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(ResponseHeadersHelper.getBaseHeaders()).body(problemDetail);
    }

    private HashMap<String,Object> mapErrors (BindingResult bindingResult){
        List<HashMap<String,Object>> errorsList = new ArrayList<>();
        for (FieldError error : bindingResult.getFieldErrors()){
            HashMap<String,Object> temp = new HashMap<>();
            temp.put("Field",error.getField());
            temp.put("Message",error.getDefaultMessage());
            errorsList.add(temp);
        }

        HashMap<String,Object> errorsMap = new HashMap<>();
        errorsMap.put("Errors",errorsList);
        return errorsMap ;
    }

}
