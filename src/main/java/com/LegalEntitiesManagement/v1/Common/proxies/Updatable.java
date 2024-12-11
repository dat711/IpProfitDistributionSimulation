package com.LegalEntitiesManagement.v1.Common.proxies;

public interface Updatable <T,D>{
    T update (T t);

    T updateFromDto (D dto);
}
