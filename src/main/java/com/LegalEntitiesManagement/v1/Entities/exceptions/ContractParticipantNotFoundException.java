package com.LegalEntitiesManagement.v1.Entities.exceptions;

public class ContractParticipantNotFoundException extends RuntimeException{
    public ContractParticipantNotFoundException(long id) {
        super(String.format("Can not find the contract participant with id: %s", id));
    }

    public ContractParticipantNotFoundException(long contractId, long stakeholderId) {
        super(String.format("Can not find the contract participant with contract id: %s and stakeholder id: %s",
                contractId, stakeholderId));
    }
}
