package com.LegalEntitiesManagement.v1.Entities.exceptions;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(long id){
        super(String.format("Can not find the Role with id: %s" ,id));
    }
}
