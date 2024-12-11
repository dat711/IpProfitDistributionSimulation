package com.LegalEntitiesManagement.v1.Common.proxies;

public interface Edge <TN> {
    TN getTarget();
    TN getSource();

    double getQuantity();
}
