package com.LegalEntitiesManagement.v1.Common.Proxies;

public interface Updatable <T,D>{
    T update (T t);

    T updateFromDto (D dto);
}
