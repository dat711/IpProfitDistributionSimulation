package com.LegalEntitiesManagement.v1.unitTests;
import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.*;
import com.LegalEntitiesManagement.v1.Entities.model.supportClass.Branch;
import com.LegalEntitiesManagement.v1.Entities.model.supportClass.MoneyNodeBranch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/* To do
*  -> provide session counter that increase on each object used on mock test
*  -> provide method for batch generation of object with overlap dependencies, and a logic to differentiate them
*  may be on String Field or ID field or priority Field
*  ->
*
* */
public class TestDataFactory {
    @Getter
    @Setter
    public static class validateContext{
        private Branch oldBranch;
        private Branch newBranch;
        private Branch mergedBranch;
        private Map<Long, Long> rewiredSourceTargetIds;
        private boolean swapRoot = false;
        private boolean swapTail = false;
        private MoneyNodeBranch examinedMoneyNodeBranch;

        public validateContext(Branch oldBranch){
            this.oldBranch = oldBranch;
        }

        public validateContext updateBranches(Branch mergedBranch, Branch newBranch){
            this.mergedBranch = mergedBranch;
            this.newBranch = newBranch;
            populateValidationContext();
            return this;
        }

        public validateContext addExaminedBranch(MoneyNodeBranch examinedMoneyNodeBranch){
            this.examinedMoneyNodeBranch = examinedMoneyNodeBranch;
            return this;
        }

        private Map<IpBasedContract, IpBasedContract> getResponsibilityMap(Branch branch){
            List<IpBasedContract> sortedContracts = branch.getSortedContract();

            if(sortedContracts.size() <= 1){
                return new HashMap<>();
            }

            Iterator<IpBasedContract> sources = sortedContracts.subList(0, sortedContracts.size() - 1).iterator();
            Iterator<IpBasedContract> targets = sortedContracts.subList(1, sortedContracts.size()).iterator();
            Map<IpBasedContract, IpBasedContract> responsibilityMap = new HashMap<>();

            while(sources.hasNext() && targets.hasNext()){
                responsibilityMap.put(sources.next(), targets.next());
            }
            return responsibilityMap;
        }

        private void populateValidationContext(){
            this.swapRoot = newBranch.getSortedContract().get(0).getContractPriority() > oldBranch.getSortedContract()
                    .get(0).getContractPriority();
            this.swapTail = newBranch.getSortedContract().get(newBranch.getSortedContract().size() - 1).getContractPriority()
                    < oldBranch.getSortedContract().get(oldBranch.getSortedContract().size() - 1).getContractPriority();

            Map<IpBasedContract, IpBasedContract> oldResponsibilityDetails = getResponsibilityMap(oldBranch);
            Map<IpBasedContract, IpBasedContract> mergedResponsibilityDetails = getResponsibilityMap(mergedBranch);
            rewiredSourceTargetIds = mergedResponsibilityDetails.entrySet().stream().filter(
                    entry -> !entry.getValue().getContractPriority().equals(
                            oldResponsibilityDetails.get(entry.getKey()).getContractPriority()
                    )
            ).collect(Collectors.toMap(
                    entry -> entry.getKey().getId(),
                    entry -> entry.getValue().getId()
            ));
        }

        public boolean validRewiredResponsibility(){
            Set<Responsibility> examinedResponsibility = examinedMoneyNodeBranch.getResponsibilities().stream()
                    .filter(responsibility ->
                            rewiredSourceTargetIds.containsKey(responsibility.getSource().getId())
                    ).collect(Collectors.toSet());
            return examinedResponsibility.stream().noneMatch(
                    responsibility -> {
                        Long sourceId = responsibility.getSource().getId();
                        Long currentTargetId = responsibility.getTarget().getId();
                        return currentTargetId.equals(
                                rewiredSourceTargetIds.get(sourceId)
                        );
                    }
            );
        }

        public boolean validResponsibilityPriorityOrder(){
            Set<Responsibility> examinedResponsibility = examinedMoneyNodeBranch.getResponsibilities().stream()
                    .filter(responsibility -> responsibility.getTarget() instanceof ContractNode)
                    .collect(Collectors.toSet());

            return examinedResponsibility.stream().allMatch(
                    responsibility -> {
                        ContractNode source = ((ContractNode) responsibility.getSource());
                        ContractNode target = ((ContractNode) responsibility.getTarget());
                        return source.getContract().getContractPriority() > target.getContract().getContractPriority();
                    }
            );
        }

        public boolean validResponsibilitySourceType(){
            return examinedMoneyNodeBranch.getResponsibilities().stream().noneMatch(
                    responsibility -> responsibility.getSource() instanceof StakeHolderLeaf
            );
        }

        public void printOutInformationOfFailedRewired(){
            Set<Responsibility> falseSourceTypeResponsibility = examinedMoneyNodeBranch.getResponsibilities().stream().filter(
                    responsibility -> responsibility.getSource() instanceof StakeHolderLeaf
            ).collect(Collectors.toSet());
        }

        public boolean validRoot(){
            if (swapRoot){
                return examinedMoneyNodeBranch.getBranchRoot().getContract().getContractPriority().equals(
                        newBranch.getSortedContract().get(0).getContractPriority()
                );
            }

            return examinedMoneyNodeBranch.getBranchRoot().getContract().getContractPriority().equals(
                    oldBranch.getSortedContract().get(0).getContractPriority());
        }

        public boolean validTail(){
            if (swapTail){
                return examinedMoneyNodeBranch.getContractNodes().get(
                        examinedMoneyNodeBranch.getContractNodes().size() - 1
                ).getContract().getContractPriority().equals(
                        newBranch.getSortedContract().get(
                                newBranch.getSortedContract().size() - 1
                        ).getContractPriority()
                );
            }

            return examinedMoneyNodeBranch.getContractNodes().get(
                    examinedMoneyNodeBranch.getContractNodes().size() - 1
            ).getContract().getContractPriority().equals(
                    oldBranch.getSortedContract().get(
                            oldBranch.getSortedContract().size() - 1
                    ).getContractPriority());
        }
    }

    @NoArgsConstructor
    public static class Counter {
        private int count = 0;
        public Long getId(){
            count++;
            return (long) count;
        }
    }


    /* Step to implement generic data generator:
    * -> identify which features is not used in the logic
    * -> identify which features is used in the logic and its purpose, like queried, compared,
    * -> identify which features is
    * */


    public static List<StakeHolder> genStakeHolders(Role role, int size, Counter counter){
        ArrayList<StakeHolder> stakeHolders = new ArrayList<>();
        for (int i = 0; i < size; i++){
            stakeHolders.add(genStakeHolder(role, counter));
        }
        return stakeHolders;
    }

    public static StakeHolder genStakeHolder(Role role, Counter counter){
        Long id = counter.getId();
        StakeHolder newStakeHolder = new StakeHolder(String.format("Mock StakeHolder %s", id), role);
        newStakeHolder.setId(id);
        return newStakeHolder;
    }

    public static Map<StakeHolder, StakeHolderLeaf> genMapLeaves(Counter moneyNodeCounter ,StakeHolder... stakeHolders){
        return Arrays.stream(stakeHolders).collect(Collectors.toMap(
                stakeHolder -> stakeHolder, stakeHolder -> TestDataFactory.syncStakeHolderLeaf(stakeHolder, moneyNodeCounter)
        ));
    }

    private static StakeHolderLeaf syncStakeHolderLeaf(StakeHolder stakeHolder,Counter moneyNodeCounter){
        StakeHolderLeaf newLeaf = new StakeHolderLeaf(stakeHolder);
        newLeaf.setId(moneyNodeCounter.getId());
        return newLeaf;
    }

    public record MultiContractParticipants(List<List<StakeHolder>> allContractsParticipant,
                                                   List<StakeHolder> nonExecutorParticipants){}

    public static MultiContractParticipants genBranchContractsParticipant(Role participantRole, Counter counter,
                                                                          int numOfParticipantPerContract,
                                                                          int numContract, StakeHolder executor){
        ArrayList<StakeHolder> nonExecutor = new ArrayList<>();
        ArrayList<List<StakeHolder>> allContractsParticipant = new ArrayList<>();

        for (int i = 0; i < numContract; i++){
            ArrayList<StakeHolder> thisContractParticipant = (ArrayList<StakeHolder>) genStakeHolders(participantRole,
                    numOfParticipantPerContract, counter);
            nonExecutor.addAll(thisContractParticipant);
            thisContractParticipant.add(executor);
            allContractsParticipant.add(thisContractParticipant);
        }

        return new MultiContractParticipants(allContractsParticipant, nonExecutor);
    }

    public static List<IpBasedContract> genBranchContracts(StakeHolder executor, List<List<StakeHolder>> participantsList,
                                                           IntellectualProperty ip, Counter contractCounter,
                                                           Counter participantCounter){
        List<IpBasedContract> allContracts = participantsList.stream().map(
                participants -> genIpBasedContract(executor, participants, ip, contractCounter, participantCounter, 0)
        ).toList();

        allContracts.forEach(contract -> {
            int priority = allContracts.size() - allContracts.indexOf(contract);
            contract.setContractPriority(priority);
        });

        return allContracts;
    }


    public static List<IpBasedContract> genBranchContractsWithPriorities(StakeHolder executor, Map<List<StakeHolder>, Integer> participantsList,
                                                           IntellectualProperty ip, Counter contractCounter,
                                                           Counter participantCounter){

        return participantsList.entrySet().stream().map(
                entry -> genIpBasedContract(executor, entry.getKey(), ip, contractCounter, participantCounter, entry.getValue())
        ).sorted(
                (a,b) -> Integer.compare(b.getContractPriority(), a.getContractPriority())
        ).toList();
    }

    public static IpBasedContract genIpBasedContract(StakeHolder executor, List<StakeHolder> participants,
                                                     IntellectualProperty ip, Counter contractCounter,
                                                     Counter participantCounter, int priority){

        IpBasedContract thisContract = new IpBasedContract("Mock Contract", LocalDate.now(), priority, ip, executor);
        double percentage = (double) 1 /participants.size();
        Set<ContractParticipant> allParticipants = participants.stream().map(
                participant -> genContractParticipant(thisContract, participant, percentage, participantCounter, participant.equals(executor))
        ).collect(Collectors.toSet());
        thisContract.setId(contractCounter.getId());
        thisContract.setContractParticipants(allParticipants);
        return thisContract;
    }

    public static ContractParticipant genContractParticipant(IpBasedContract contract, StakeHolder participant,
                                                             double percentage, Counter counter, boolean isExecutor){
        ContractParticipant newParticipant = new ContractParticipant(contract, percentage, isExecutor, participant);
        newParticipant.setId(counter.getId());
        return newParticipant;
    }

    public static Responsibility genLeafResponsibility(ContractNode sourceNode, StakeHolderLeaf targetLeaf, Counter responsibilityCounter, double percentage){
        Responsibility newResponsibility =  new Responsibility(targetLeaf, sourceNode, percentage);
        newResponsibility.setId(responsibilityCounter.getId());

        return newResponsibility;
    }

    public static Responsibility genResponsibilityBetweenNode(ContractNode sourceNode, ContractNode targetNode, Counter responsibilityCounter,
                                                              double percentage){
        Responsibility newResponsibility =  new Responsibility(targetNode, sourceNode, percentage);
        newResponsibility.setId(responsibilityCounter.getId());
        return newResponsibility;
    }

    private static Set<Responsibility> singleResponsibilitySet(Responsibility responsibility){
        Set<Responsibility> currentRes = new HashSet<>();
        currentRes.add(responsibility);
        return currentRes;
    }

    public static List<ContractNode> genContractNodes(Collection<IpBasedContract> existedContract, Counter moneyNodeCounter,
                                                      Counter responsibilityCounter, StakeHolderLeaf executorLeaf){
        List<IpBasedContract> sortedContracts = existedContract.stream().sorted(
                Comparator.comparingInt(contract -> - contract.getContractPriority())
        ).toList();

        List<ContractNode> contractNodes = sortedContracts.stream().map(
                contract -> {
                    ContractNode contractNode = new ContractNode(contract);
                    contractNode.setId(moneyNodeCounter.getId());
                    return contractNode;
                }
        ).toList();

        List<MoneyNode> targetNode = contractNodes.size() > 1 ? new ArrayList<>(contractNodes.subList(1, contractNodes.size()).stream().map(
                contractNode -> (MoneyNode) contractNode
        ).toList()) : new ArrayList<>();
        targetNode.add(executorLeaf);

        Iterator<ContractNode> sources = contractNodes.iterator();
        Iterator<MoneyNode> targets = targetNode.iterator();
        while (targets.hasNext() && sources.hasNext()){
            ContractNode source = sources.next();
            MoneyNode target = targets.next();

            if (target instanceof StakeHolderLeaf){
                Responsibility responsibility = genLeafResponsibility(source,(StakeHolderLeaf) target, responsibilityCounter, 0);
                source.setDownStreamEdges(singleResponsibilitySet(responsibility));
//                printResponsibilityDetails(responsibility);
                break;
            }

            Responsibility responsibility = genResponsibilityBetweenNode(source, (ContractNode) target, responsibilityCounter, 0);
            source.setDownStreamEdges(singleResponsibilitySet(responsibility));
        }

        return contractNodes;
    }

    public static void addNonExecutorTargetResponsibility(ContractNode contractNode, Counter responsibilityCounter,
                                                          Map<StakeHolder, StakeHolderLeaf> mapLeaves){
        Set<ContractParticipant> nonExecutorParticipants = contractNode.getContract().getContractParticipants().stream().filter(
                participant -> !participant.getIsExecutor()
        ).collect(Collectors.toSet());

        ContractParticipant executorParticipant = contractNode.getContract().getContractParticipants().stream().filter(
                ContractParticipant::getIsExecutor).findFirst().orElseThrow();

        contractNode.getDownStreamEdges().forEach(
                responsibility -> responsibility.setPercentage(executorParticipant.getPercentage())
        );

        contractNode.getDownStreamEdges().addAll(
                nonExecutorParticipants.stream().map(
                    participant -> {
                        StakeHolderLeaf target = mapLeaves.get(participant.getStakeholder());
                        Responsibility responsibility = genLeafResponsibility(contractNode, target, responsibilityCounter, participant.getPercentage());
//                        printResponsibilityDetails(responsibility);
                        return responsibility;
                    }
                ).collect(Collectors.toSet())
        );
    }

    private static List<ContractNode> genFullContractNodeOfBranch(Collection<IpBasedContract> existedContract, Counter moneyNodeCounter,
                                                           Counter responsibilityCounter, Map<StakeHolder, StakeHolderLeaf> mapLeaves){
        StakeHolderLeaf executorLeaf =  mapLeaves.get(existedContract.stream().map(IpBasedContract::getExecutor).findFirst().orElseThrow());
        List<ContractNode> simpleContractNodes =  genContractNodes(existedContract, moneyNodeCounter, responsibilityCounter, executorLeaf);
        simpleContractNodes.forEach(contractNode -> addNonExecutorTargetResponsibility(contractNode, responsibilityCounter, mapLeaves));
        return simpleContractNodes;
    }


    private static void connectGenContractBranch(Set<ContractNode> allContractNode, Map<StakeHolder, IpBasedContract> topContractOfBranches,
                                                 Map<StakeHolder, StakeHolderLeaf> mapLeaves){
        Set<Responsibility> allLeafResponsibility = allContractNode.stream().map(ContractNode::getDownStreamEdges)
                .flatMap(Set::stream).filter(responsibility -> responsibility.getTarget() instanceof StakeHolderLeaf)
                .filter(responsibility -> {
                    ContractNode source = (ContractNode) responsibility.getSource();
                    StakeHolderLeaf target = (StakeHolderLeaf) responsibility.getTarget();
                    return !source.getContract().getExecutor().equals(target.getStakeHolder());
                 })
                .collect(Collectors.toSet());

        Set<IpBasedContract> allTopContracts = new HashSet<>(topContractOfBranches.values());

        Map<StakeHolderLeaf, ContractNode> contractNodeMap = allContractNode.stream().filter(
                contractNode -> allTopContracts.contains(contractNode.getContract())
        ).collect(Collectors.toMap(
                contractNode ->  mapLeaves.get(contractNode.getContract().getExecutor()),
                contractNode -> contractNode
        ));


        allLeafResponsibility.forEach(responsibility -> {
            StakeHolderLeaf currentTarget = (StakeHolderLeaf) responsibility.getTarget();
            if(contractNodeMap.containsKey(currentTarget)){
                responsibility.setTarget(contractNodeMap.get(currentTarget));
            }
        });
    }


    public record treeContractNodeInfo (Map<Long, ContractNode> mapContractNodeByContractId,
                                        Map<Long, Collection<Responsibility>> mapDownStreamEdges,
                                        Map<Long, Collection<Responsibility>> mapUpperStreamEdges,
                                        ContractNode topNode){}

    public static treeContractNodeInfo genTreeContractNodes(Collection<IpBasedContract> existedContract, Counter moneyNodeCounter,
                                                          Counter responsibilityCounter, Map<StakeHolder, StakeHolderLeaf> mapLeaves){
        Map<StakeHolder, List<IpBasedContract>> groupedContract = existedContract.stream().collect(Collectors.groupingBy(
                Contract::getExecutor
        )).entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream().sorted(
                        (c1, c2) -> Integer.compare(c2.getContractPriority(), c1.getContractPriority())
                ).toList()
        ));

        Map<StakeHolder, IpBasedContract> topContractByExecutor = groupedContract.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().get(0)
        ));

        StakeHolder topExecutor = topContractByExecutor.keySet().stream()
                .max(Comparator.comparingInt(e -> e.getRole().getPriority())).orElseThrow();
        IpBasedContract rootContract = topContractByExecutor.get(topExecutor);

        Map<StakeHolder, List<ContractNode>>  addedDownStreamEdgesContractNode = groupedContract.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> genFullContractNodeOfBranch(entry.getValue(), moneyNodeCounter,
                                responsibilityCounter, mapLeaves)
                ));

        Set<ContractNode> allContractNode = addedDownStreamEdgesContractNode.values().stream()
                .map(HashSet::new).flatMap(Set::stream).collect(Collectors.toSet());
        ContractNode treeRoot = allContractNode.stream().filter(
                contractNode -> contractNode.getContract().getId().equals(rootContract.getId())
        ).findFirst().orElseThrow();

        connectGenContractBranch(allContractNode, topContractByExecutor, mapLeaves);

        Map<Long, ContractNode> mapContractNodeByContractId = allContractNode.stream().collect(Collectors.toMap(
                contractNode -> contractNode.getContract().getId(), contractNode -> contractNode
        ));

        Map<Long, Collection<Responsibility>> mapDownStreamEdges = allContractNode.stream().collect(Collectors.toMap(
                ContractNode::getId, MoneyNode::getDownStreamEdges
        ));

        Map<Long, Collection<Responsibility>> mapUpperStreamEdges = allContractNode.stream().map(
                ContractNode::getDownStreamEdges).flatMap(Set::stream).collect(Collectors.toSet())
                .stream().filter(responsibility -> responsibility.getTarget() instanceof ContractNode)
                .collect(Collectors.groupingBy(
                        responsibility -> responsibility.getTarget().getId()
                )).entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new HashSet<>(entry.getValue())
                ));

        return new treeContractNodeInfo(mapContractNodeByContractId, mapDownStreamEdges, mapUpperStreamEdges, treeRoot);
    }

    @NoArgsConstructor
    @Setter
    @Getter
    public static class FullTreeValidationContext {
        private ContractNode headNode;

        private Set<Responsibility> allResponsibilities;

        private Map<Long,Map<Long, Double>> responsibilitiesDetail;

        private Map<Long, Map<Long, Responsibility>> responsibilityMapSourceTarGet;

        private Map<StakeHolder, StakeHolderLeaf> mapLeaves;

        private Map<Long, Long> contractNodesMapByContractId;

        private Set<ContractNode> allNodes;

        private ExpectedChanges expectedChanges;

        public record ExpectedChanges(Set<ContractParticipant> deletedParticipants,
                                      Set<ContractParticipant> newParticipants,
                                      Set<ContractParticipant> updatedParticipants,
                                      IpBasedContract deletedContract,
                                      Set<IpBasedContract> newContracts,
                                      IpBasedContract updatedContract){}

        public FullTreeValidationContext populateContext(){
            if (headNode == null || mapLeaves == null){
                throw new IllegalStateException("The head node and mapLeaves must be injected before populate other properties");
            }

            this.allResponsibilities = new HashSet<>();
            this.allNodes = new HashSet<>();
            allNodes.add(headNode);
            travelDown(headNode);
            populateMaps();
            return this;
        }

        private void travelDown(ContractNode node){
            Set<Responsibility> currentResponsibility = node.getDownStreamEdges();
            allResponsibilities.addAll(currentResponsibility);
            currentResponsibility.forEach(
                    responsibility -> {
                        MoneyNode target = responsibility.getTarget();
                        if (target instanceof ContractNode castedTarget){
                            allNodes.add(castedTarget);
                            travelDown(castedTarget);
                        }
                    }
            );
        }

        private void populateMaps(){
            Map<Long, List<Responsibility>> mapBySourceId = allResponsibilities.stream().collect(Collectors.groupingBy(
                    responsibility -> responsibility.getSource().getId()
            ));


            responsibilitiesDetail = mapBySourceId.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream().collect(Collectors.toMap(
                            responsibility -> responsibility.getTarget().getId(),
                            Responsibility::getPercentage
                    ))
            ));

            responsibilityMapSourceTarGet = mapBySourceId.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream().collect(Collectors.toMap(
                            responsibility -> responsibility.getTarget().getId(),
                            responsibility -> responsibility
                    ))
            ));

            contractNodesMapByContractId = allNodes.stream().collect(Collectors.toMap(
                    node -> node.getContract().getId(),
                    MoneyNode::getId
            ));
        }

        public boolean contractIsNotDeleted(){
            if (expectedChanges.deletedContract == null){
                return false;
            }

            return allNodes.stream().anyMatch(
                    contractNode -> {
                        Long id = contractNode.getContract().getId();
                        return expectedChanges.deletedContract.getId().equals(id);
                    }
            );
        }

        public boolean contractsAreNotAdded(){
            if (expectedChanges.newContracts == null || expectedChanges.newContracts.isEmpty()){
                return false;
            }

            return allNodes.stream().allMatch(
                    contractNode -> {
                        int priority = contractNode.getContract().getContractPriority();
                        StakeHolder executor = contractNode.getContract().getExecutor();
                        return expectedChanges.newContracts.stream().noneMatch(
                                contract -> contract.getContractPriority().equals(priority)
                                        && contract.getExecutor().equals(executor)
                        );
                    }
            );
        }

        public boolean contractIsNotUpdated(){
            if (expectedChanges.updatedContract == null ){
                return false;
            }

            Contract currentContract = expectedChanges.updatedContract;

            return allNodes.stream().anyMatch(
                    node -> {
                        Contract contract = node.getContract();
                        if (!contract.getId().equals(currentContract.getId())){
                            return false;
                        }

                        return contract.getContractPriority().equals(currentContract.getContractPriority())
                                && contract.getDescription().equals(currentContract.getDescription())
                                && contract.getContractActiveDate().equals(currentContract.getContractActiveDate());
                    }
            );
        }

        public boolean participantsAreNotAdded(){
            if (expectedChanges.newParticipants == null){
                return false;
            }

            return participantsIsNotAccuratePresented(expectedChanges.newParticipants);
        }

        public boolean participantsAreNotUpdated(){
            if (expectedChanges.updatedParticipants == null){
                return false;
            }

            return participantsIsNotAccuratePresented(expectedChanges.updatedParticipants);
        }

        private boolean participantsIsNotAccuratePresented(Set<ContractParticipant> participants){
            if (participants.isEmpty()){
                return false;
            }

            Set<Long> participantsContractId = participants.stream().map(ContractParticipant::getContract)
                    .map(Contract::getId).collect(Collectors.toSet());
            Set<Long> copyKey = new HashSet<>(contractNodesMapByContractId.keySet());
            copyKey.retainAll(participantsContractId);
            participantsContractId.removeAll(copyKey);

            if (!participantsContractId.isEmpty()){
                return true;
            }

            return participants.stream().noneMatch(this::checkValidParticipant);
        }

        private boolean checkValidParticipant(ContractParticipant participant){
            Long contractId = participant.getContract().getId();
            Long contractNodeId = contractNodesMapByContractId.get(contractId);
            StakeHolder stakeHolder = participant.getStakeholder();
            Map<Long,Responsibility> examinedResponsibility = responsibilityMapSourceTarGet.get(contractNodeId);

            if (examinedResponsibility.containsKey(mapLeaves.get(stakeHolder).getId())){
                Responsibility responsibility = examinedResponsibility.get(mapLeaves.get(stakeHolder).getId());
                return responsibility.getPercentage() == participant.getPercentage();
            }

            return examinedResponsibility.values().stream().filter(
                            res -> res.getTarget() instanceof ContractNode
                    ).filter(res -> ((ContractNode) res.getTarget())
                            .getContract().getExecutor().equals(stakeHolder))
                    .anyMatch(responsibility -> responsibility.getPercentage() == participant.getPercentage());
        }

        public boolean participantsAreNotDeleted(){
            if (expectedChanges.deletedParticipants == null || expectedChanges.deletedParticipants.isEmpty()){
                return false;
            }

            return expectedChanges.deletedParticipants.stream().anyMatch(
                    participant -> {
                        Long contractId = participant.getContract().getId();
                        if (!contractNodesMapByContractId.containsKey(contractId)){
                            return false;
                        }
                        Long contractNodeId = contractNodesMapByContractId.get(contractId);
                        StakeHolder stakeHolder = participant.getStakeholder();

                        Map<Long,Responsibility> examinedResponsibility = responsibilityMapSourceTarGet.get(contractNodeId);
                        return examinedResponsibility.containsKey(mapLeaves.get(stakeHolder).getId());
                    }
            );
        }

        public boolean validResponsibilities(){
            return allResponsibilities.stream().allMatch(
                    responsibility -> {
                        if (responsibility.getSource() instanceof StakeHolderLeaf) {
                            return false;
                        }
                        if (responsibility.getTarget() instanceof ContractNode target){
                            ContractNode source = (ContractNode) responsibility.getSource();

                            if(source.getContract().getExecutor().getRole().getPriority() > target.getContract().getExecutor().getRole().getPriority()){
                                return true;
                            }

                            return source.getContract().getContractPriority() > target.getContract().getContractPriority();
                        }
                        return true;
                    }
            );
        }
    }
}
