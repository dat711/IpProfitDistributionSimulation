package com.LegalEntitiesManagement.v1.Entities.model.supportClass;

import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractViolatedException.ContractValidationFailed;
import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import com.LegalEntitiesManagement.v1.Entities.model.IpBasedContract;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import com.LegalEntitiesManagement.v1.Entities.model.supportClass.utilClass;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
public class Branch {
    private Set<IpBasedContract> contracts;

    private StakeHolder executor;

    private Set<StakeHolder> nonExecutorSet;

    private List<IpBasedContract> sortedContract;

    public Branch(Collection<IpBasedContract> contracts){
        this.contracts = new HashSet<>(contracts);
        populateFields();
    }

    private List<IpBasedContract> sortContractByPriority(Collection<IpBasedContract> contracts){
        return contracts.stream()
                .sorted((c1,c2) -> Integer.compare(c2.getContractPriority(), c1.getContractPriority())).toList();
    }

    private StakeHolder findExecutor(List<IpBasedContract> contracts){
        return contracts.get(0).getExecutor();
    }

    private Set<StakeHolder> findNonExecutors(List<IpBasedContract> contracts){
        List<Set<ContractParticipant>> participantsByContracts = contracts.stream().map(this::findContractParticipants).toList();
        List<Set<StakeHolder>> stakeHoldersByContracts = participantsByContracts.stream()
                .map(set -> set.stream().map(ContractParticipant::getStakeholder).collect(Collectors.toSet()))
                .toList();
        stakeHoldersByContracts.forEach(set->set.remove(executor));
        validateInternalBranchTarget(stakeHoldersByContracts);
        return stakeHoldersByContracts.stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    private Set<ContractParticipant> findContractParticipants(IpBasedContract contract){
        if (contract.getContractParticipants() == null || contract.getContractParticipants().isEmpty()){
            throw new ContractValidationFailed("Contract must be injected with participants before delegate to branch building step");
        }
        return contract.getContractParticipants();
    }

    private void populateFields(){
        sortedContract = sortContractByPriority(this.contracts);
        executor = findExecutor(sortedContract);
        nonExecutorSet = findNonExecutors(sortedContract);
        validateUniquePriorities(sortedContract, executor);
    }

    private void validateInternalBranchTarget(Collection<Set<StakeHolder>> nonExecutorStakeHolder){
        if (!utilClass.noOverlapNonExecutorStakeHolder(nonExecutorStakeHolder)) {
            Set<StakeHolder> overlapping = utilClass.findOverlapping(nonExecutorStakeHolder);
            throw new ContractValidationFailed("Non Executor Stakeholders appear in multiple contract of the same branch: " +
                    overlapping.stream().map(StakeHolder::getName).collect(Collectors.joining(", ")));
        }
    }

    public void addBranch(Branch newBranch){
        validateSameExecutorBranch(newBranch);
        this.contracts.addAll(newBranch.getContracts());
        populateFields();
    }

    private void validateSameExecutorBranch(Branch newBranch){
        if(newBranch.executor.equals(executor)){return;}
        throw new ContractValidationFailed("The two branch attempt to merge must have the same executor");
    }

    private void validateUniquePriorities(List<IpBasedContract> contracts, StakeHolder executor) {
        if (contracts == null || contracts.isEmpty()) {
            return;
        }

        // Find duplicated priorities using streams
        Map<Integer, Long> priorityCounts = contracts.stream()
                .map(Contract::getContractPriority)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));

        // Find all priorities that appear more than once
        String duplicatePriorities = priorityCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> "Priority " + entry.getKey() + " appears " + entry.getValue() + " times")
                .collect(Collectors.joining("\n"));
        String errorMessage = String.format("Failed validating contracts group of executor with id: %s/n And with details: %s",
                executor.getId(), duplicatePriorities);

        if (!duplicatePriorities.isEmpty()) {
            throw new ContractValidationFailed(errorMessage);
        }
    }

    // return collections of executor that exclude the executor of this branch and is within this branch's target
    public Set<StakeHolder> getExamined(Collection<StakeHolder> examinedStakeHolders){
        return examinedStakeHolders.stream().filter(stakeHolder -> !stakeHolder.equals(executor) && haveThisStakeHolder(stakeHolder))
                .collect(Collectors.toSet());
    }

    public boolean haveThisStakeHolder(StakeHolder stakeHolder){
        return this.nonExecutorSet.contains(stakeHolder);
    }

    public boolean isHigherEqualExecutor(StakeHolder stakeHolder){
        return stakeHolder.getRole().getPriority() >= executor.getRole().getPriority();
    }

    public boolean haveTarget(Branch branch){
        return this.haveThisStakeHolder(branch.getExecutor());
    }
}
