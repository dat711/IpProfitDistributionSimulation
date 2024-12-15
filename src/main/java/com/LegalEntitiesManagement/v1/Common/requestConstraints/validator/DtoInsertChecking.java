package com.LegalEntitiesManagement.v1.Common.requestConstraints.validator;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DtoInsertChecking {

    public static record NullInsertErrorDetails(boolean isValid, String message){}

    public static NullInsertErrorDetails isInsertRequestValid(Class<?> tClass, Object request){
        List<Field> fields = List.of(tClass.getDeclaredFields());
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
        fields.forEach(field -> field.setAccessible(false));
        List<String> temp = Arrays.stream(tClass.getName().split("\\.")).toList();
        String className = temp.get(temp.size() - 1);
        String message = isValid ? "" : String.format("Here is the list of fields that is null in create request of object %s: %s",
                className,
                nullFields.stream().map(field -> field + " ").toList());

        return new NullInsertErrorDetails(isValid, message);
    }
}
