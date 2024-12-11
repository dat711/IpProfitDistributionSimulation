package com.LegalEntitiesManagement.v1.Entities.exceptions;

public class IpNotFoundException extends RuntimeException{
    public IpNotFoundException(String message) {
        super(message);
    }

    public IpNotFoundException(long id){
        super(String.format("Can not find the Intellectual Property with id: %s" ,id));
    }
}
