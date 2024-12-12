package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DtoInsertChecking {

    public static record NullInsertErrorDetails(boolean isValid, String message){}

    public static NullInsertErrorDetails isInsertRequestValid(Class<?> tClass, Object request){
        List<Field> fields = List.of(tClass.getFields());
        ArrayList<String> nullFields = new ArrayList<>();
        boolean isValid = fields.stream()
                .peek(field -> field.setAccessible(true))
                .filter(field -> !field.getName().equals("id"))
                .allMatch(field -> {
                    try {
                        if(field.get(request) != null){
                            return true;
                        }
                        nullFields.add(field.getName());
                        return false;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
        String message = isValid ? "" : String.format("Here is the list of fields that is null in create request of object %s: %s",
                tClass.getName(),
                nullFields.stream().map(field -> " " + field+", "));

        return new NullInsertErrorDetails(isValid, message);
    }
}
