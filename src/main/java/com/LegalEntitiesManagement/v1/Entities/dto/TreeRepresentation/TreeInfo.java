package com.LegalEntitiesManagement.v1.Entities.dto.TreeRepresentation;

import com.LegalEntitiesManagement.v1.Entities.dto.IntellectualPropertyDto;

public record TreeInfo(String IpLink, IntellectualPropertyDto IpDetail, NodeDto rootNode) {
}
