package com.LegalEntitiesManagement.v1.Common.aspects.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse<T> {
    private String message;
    private T data;

    public static <T> SuccessResponse<T> successResponse(T data, String message) {
        SuccessResponse<T> response = new SuccessResponse<>();
        response.setData(data);
        response.setMessage(message);
        return response;
    }
}
