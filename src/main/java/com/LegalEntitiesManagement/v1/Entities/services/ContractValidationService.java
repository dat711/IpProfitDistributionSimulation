package com.LegalEntitiesManagement.v1.Entities.services;

import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractViolatedException.ContractValidationFailed;
import com.LegalEntitiesManagement.v1.Entities.model.*;

import java.util.*;
public class ContractValidationService {
    public static ContractValidationService instance = new ContractValidationService();
    private ContractValidationService(){}

    public void validateContractWithExceptions(Contract contract, Set<ContractParticipant> participants) {
        if (!hasValidParticipants(participants)) {
            throw new ContractValidationFailed("Contract participants is invalid");
        }

        if (!hasValidExecutor(participants)) {
            throw new ContractValidationFailed("Every contract must have exactly 1 executors");
        }

        if (!hasValidPercentages(participants)) {
            throw new ContractValidationFailed("Percentage distribution validation failed");
        }

        if (contract instanceof IpBasedContract ipContract) {
            if (ipContract.getIntellectualProperty() == null) {
                throw new ContractValidationFailed("IP-based contract must have an associated intellectual property");
            }
        }
    }

    public boolean hasValidParticipants(Set<ContractParticipant> participants) {
        if (participants == null || participants.isEmpty()) {
            return false;
        }

        return participants.stream()
                .allMatch(participant -> participant.getStakeholder() != null);
    }

    public boolean hasValidExecutor(Set<ContractParticipant> participants) {
        if (participants == null) {
            return false;
        }

        long executorCount = participants.stream()
                .filter(ContractParticipant::getIsExecutor)
                .count();

        return executorCount == 1;
    }

    public boolean hasValidPercentages(Set<ContractParticipant> participants) {
        // Check individual percentages
        boolean validIndividualPercentages = participants.stream()
                .allMatch(participant ->
                        participant.getPercentage() != null &&
                                participant.getPercentage() > 0 &&
                                participant.getPercentage() <= 100);

        if (!validIndividualPercentages) {
            return false;
        }

        // Check total percentage
        double totalPercentage = participants.stream()
                .mapToDouble(ContractParticipant::getPercentage)
                .sum();

        return totalPercentage == 100.0;
    }
}
