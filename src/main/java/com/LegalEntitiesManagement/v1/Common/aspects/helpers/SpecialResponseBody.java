package com.LegalEntitiesManagement.v1.Common.aspects.helpers;

import java.util.HashMap;

public class SpecialResponseBody {
    public static HashMap<String,String> noContentResponse(String fieldName){
        String message = String.format("There is no record of object %s", fieldName);
        HashMap<String,String> noContentBodyResponse = new HashMap<>();
        noContentBodyResponse.put("message",message);
        return noContentBodyResponse;
    }

    public static HashMap<String,String> deleteObject(String object, long Id){
        String message = String.format("The %s with id %s is removed", object, Id);
        HashMap<String,String> deletedResponse = new HashMap<>();
        deletedResponse.put("message", message);
        return deletedResponse;
    }
}
