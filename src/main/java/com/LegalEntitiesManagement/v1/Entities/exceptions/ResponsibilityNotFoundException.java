package com.LegalEntitiesManagement.v1.Entities.exceptions;

public class ResponsibilityNotFoundException extends RuntimeException{
    public ResponsibilityNotFoundException(Long id) {
        super(String.format("Cannot find the responsibility with id: %s", id));
    }
}
