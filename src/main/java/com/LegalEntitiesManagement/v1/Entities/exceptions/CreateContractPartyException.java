package com.LegalEntitiesManagement.v1.Entities.exceptions;

public class CreateContractPartyException extends RuntimeException{

    private static final String nullEntityIdMessage = "The entity id must not be null";
    private static final String nullEntityObjectMessage = "The entity must not be null";
    private static final String invalidTypeMessage = "The entity must either be a contract or a stakeholder";
    public enum ExceptionType{
        NullEntityID(nullEntityIdMessage),NullEntityObjectType(nullEntityObjectMessage),InvalidType(invalidTypeMessage)  ;
        private final String message;
        ExceptionType(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }

    public CreateContractPartyException(ExceptionType Type) {
        super(Type.getMessage());
    }
}
