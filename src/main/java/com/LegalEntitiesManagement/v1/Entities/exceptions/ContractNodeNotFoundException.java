package com.LegalEntitiesManagement.v1.Entities.exceptions;

public class ContractNodeNotFoundException extends RuntimeException{
    public ContractNodeNotFoundException(Long id) {
        super(String.format("Can not find the contract node with id: %s", id));
    }

    public ContractNodeNotFoundException(String message){
        super(message);
    }
}
