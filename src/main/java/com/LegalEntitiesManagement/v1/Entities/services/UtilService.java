package com.LegalEntitiesManagement.v1.Entities.services;

import com.LegalEntitiesManagement.v1.Entities.exceptions.TypeNotMatchException;

public class UtilService {
    public static <E,D, Mapper> E verifyAndGetEntity(Object object, Class<E> entityClass, Class<D> dtoClass,
                                                     Mapper mapper, String message){
        if (object == null){
            throw new RuntimeException("Object should not be null");
        }

        if (entityClass.isInstance(object)){
            return entityClass.cast(object);
        }

        if (dtoClass.isInstance(object)){
            try {
                var toEntityMethod = mapper.getClass().getMethod("toEntity", dtoClass);
                @SuppressWarnings("unchecked")
                E result = (E) toEntityMethod.invoke(mapper, dtoClass.cast(object));
                return result;
            } catch (Exception e) {
                throw new TypeNotMatchException("Failed to convert DTO to entity using mapper");
            }
        }
        throw new TypeNotMatchException(message);
    }


}
