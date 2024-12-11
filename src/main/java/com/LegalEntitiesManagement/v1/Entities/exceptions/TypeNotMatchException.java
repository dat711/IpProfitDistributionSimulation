package com.LegalEntitiesManagement.v1.Entities.exceptions;

public class TypeNotMatchException extends RuntimeException{
    public TypeNotMatchException(String message){
        super(String.format("The Type is not match system requirements with details: %s", message));
    }
}
