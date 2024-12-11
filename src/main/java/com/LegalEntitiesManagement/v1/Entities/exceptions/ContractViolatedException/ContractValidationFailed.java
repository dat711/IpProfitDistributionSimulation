package com.LegalEntitiesManagement.v1.Entities.exceptions.ContractViolatedException;

public class ContractValidationFailed extends RuntimeException{
    public ContractValidationFailed(String message) {
        super(message);
    }
}
