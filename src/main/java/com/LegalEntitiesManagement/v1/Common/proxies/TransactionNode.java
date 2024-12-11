package com.LegalEntitiesManagement.v1.Common.proxies;

import java.util.Set;

public interface TransactionNode<TE> {
    Set<TE> getUpStreamEdges();
    Set<TE> getDownStreamEdges();
}
