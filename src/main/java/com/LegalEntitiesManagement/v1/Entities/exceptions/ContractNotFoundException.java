package com.LegalEntitiesManagement.v1.Entities.exceptions;

public class ContractNotFoundException extends RuntimeException{
    public ContractNotFoundException(long id) {
        super(String.format("Can not find the contract with id: %s" ,id));
    }
}
