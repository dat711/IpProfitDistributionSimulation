package com.LegalEntitiesManagement.v1.Common.Proxies;

public interface Edge <TN> {
    TN getTarget();
    TN getSource();

    double getQuantity();
}
