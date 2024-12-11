package com.LegalEntitiesManagement.v1.Common.proxies;

import java.util.List;

public interface BaseService <T,D,ID>{
    T findById(ID id);
    T save(T entity);
    boolean existsById(ID id);
    void deleteById(ID id);
    List<T> findAll();

    T saveFromDto (D dto);


    void verify (Object T);
}
