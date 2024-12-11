package com.LegalEntitiesManagement.v1.Entities.exceptions;

public class StakeHolderNotFoundException extends RuntimeException{
    public StakeHolderNotFoundException(long id) {
        super(String.format("Can not find the StakeHolder with id: %s" ,id));
    }

    public StakeHolderNotFoundException(String message) {
        super(message);
    }
}
