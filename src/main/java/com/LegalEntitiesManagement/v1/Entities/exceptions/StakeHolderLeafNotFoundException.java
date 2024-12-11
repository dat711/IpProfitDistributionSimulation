package com.LegalEntitiesManagement.v1.Entities.exceptions;

public class StakeHolderLeafNotFoundException extends RuntimeException{
    public StakeHolderLeafNotFoundException(Long id) {
        super(String.format("Cannot find the stakeholder leaf node with id: %s", id));
    }
}
