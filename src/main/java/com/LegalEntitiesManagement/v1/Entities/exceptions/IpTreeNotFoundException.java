package com.LegalEntitiesManagement.v1.Entities.exceptions;

public class IpTreeNotFoundException extends RuntimeException {
    public IpTreeNotFoundException(Long id) {
        super(String.format("Cannot find the IP tree with id: %s", id));
    }

    public IpTreeNotFoundException(String dependencies, Long id){
        super(String.format("The IpTree associate with %s that have id: %s is not found",dependencies, id));
    }
}
