package com.LegalEntitiesManagement.v1.Entities.exceptions.GraphViolatedException;

public class StakeHolderLeafNotRegisteredException extends RuntimeException{
    public StakeHolderLeafNotRegisteredException(Long id) {
        super(String.format("The StakeHolder Leaf with stakeholder id: %s is not registered", id));
    }

    public StakeHolderLeafNotRegisteredException(String message){
        super(message);
    }
}
