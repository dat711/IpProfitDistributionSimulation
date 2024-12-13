package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ValidatorHelpers {
    public static Long getPathID(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        HttpServletRequest request = attributes.getRequest();
        return (Long) request.getAttribute("currentPathVariable");
    }
}
