package com.LegalEntitiesManagement.v1.Entities.model.supportClass;

import com.LegalEntitiesManagement.v1.Entities.model.Contract;
import com.LegalEntitiesManagement.v1.Entities.model.ContractParticipant;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.MoneyNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.Responsibility;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import com.LegalEntitiesManagement.v1.Entities.model.IpBasedContract;
import com.LegalEntitiesManagement.v1.Entities.model.StakeHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class MoneyNodeBranch {
    private Branch branch;
    private StakeHolderLeaf executorLeaf;
    private Map<StakeHolderLeaf, Responsibility> switchTarget;
    private ContractNode branchRoot;
    private List<ContractNode> contractNodes;
    private Collection<Responsibility> responsibilities;
    private Map<StakeHolder, StakeHolderLeaf> mapLeaves;

    public boolean containTarget(MoneyNodeBranch otherBranch){
        return this.getSwitchTarget().containsKey(otherBranch.executorLeaf);
    }

    public void switchTarget(MoneyNodeBranch otherBranch){
        Responsibility theCurrentResponsibility = switchTarget.get(otherBranch.executorLeaf);
        theCurrentResponsibility.setTarget(otherBranch.branchRoot);
    }

    @Getter
    public static class RelationShipChanges{
        private final HashMap<IpBasedContract, IpBasedContract> newIsBefore;
        private final Set<StakeHolder> involvedStakeHolder;
        private final Set<IpBasedContract> toAddContract;

        private final IpBasedContract oldLeadContract;
        private final IpBasedContract newLeadContract;

        public RelationShipChanges(Branch original, Branch merged){
            toAddContract = merged.getContracts();
            toAddContract.removeAll(original.getContracts());
            oldLeadContract = original.getSortedContract().get(0);
            newLeadContract = merged.getSortedContract().get(0);
            newIsBefore = getRelationShip(merged);

            involvedStakeHolder = new HashSet<>();
            involvedStakeHolder.add(merged.getExecutor());
            Set<StakeHolder> nonExecutorMerged = merged.getNonExecutorSet();
            nonExecutorMerged.removeAll(original.getNonExecutorSet());
            involvedStakeHolder.addAll(nonExecutorMerged);
        }

        private HashMap<IpBasedContract, IpBasedContract> getRelationShip(Branch branch){
            List<IpBasedContract> contracts = branch.getSortedContract();
            Iterator<IpBasedContract> before = contracts.subList(0, contracts.size() - 1).iterator();
            Iterator<IpBasedContract> after = contracts.subList(1, contracts.size()).iterator();
            HashMap<IpBasedContract, IpBasedContract> res = new HashMap<>();
            while (before.hasNext() && after.hasNext()){
                    res.put(before.next(), after.next());
            }
            return res;
        }
    }

    public static class Loader {
        private Branch branch;
        private StakeHolderLeaf executorLeaf;
        private Map<StakeHolderLeaf, Responsibility> switchTarget;
        private ContractNode branchRoot;
        private List<ContractNode> contractNodes;
        private Collection<Responsibility> responsibilities;
        private Map<StakeHolder, StakeHolderLeaf> mapLeaves;

        private RelationShipChanges relationShipsChange;
        private Function<ContractNode,ContractNode> newContractNodeBuilder;

        private Function<Collection<IpBasedContract>, Collection<ContractNode>> contractNodesLoader;

        private BiFunction<Long, Long, Optional<Responsibility>> tailResponsibilityLoader;

        private Function<Collection<Long>, Map<Long,Collection<Responsibility>>> upperNodeLoader;

        public Loader (Branch branch) {
            this.branch = branch;
        }

        public Loader addNewContractNodeBuilder(Function<ContractNode,ContractNode> contractNodeSupplier){
            this.newContractNodeBuilder = contractNodeSupplier;
            return this;
        }

        public Loader addNewContractNodeLoader(Function<Collection<IpBasedContract>, Collection<ContractNode>> contractNodesLoader){
            this.contractNodesLoader = contractNodesLoader;
            return this;
        }

        public Loader addUpperNodeLoader(Function<Collection<Long>, Map<Long,Collection<Responsibility>>> upperNodeLoader){
            this.upperNodeLoader = upperNodeLoader;
            return this;
        }

        public Loader addResponsibilityLoader(BiFunction<Long, Long, Optional<Responsibility>> responsibilityLoader){
            this.tailResponsibilityLoader = responsibilityLoader;
            return this;
        }

        public Loader addBranch(Branch newBranch){
            if (branch == null){
                throw new IllegalStateException("Original branch must be injected before adding new branch");
            }

            Branch original = new Branch(branch.getContracts());
            branch.addBranch(newBranch);

            relationShipsChange = new RelationShipChanges(original, branch);
            return this;
        }

        public Loader getLeaves(Map<StakeHolder, StakeHolderLeaf> allLeaves){
            if (relationShipsChange == null){
                throw new IllegalStateException("The new branch must be add before inject the mapLeaves");
            }
            this.mapLeaves = allLeaves.entrySet().stream().filter(
                    pair -> relationShipsChange.getInvolvedStakeHolder().contains(pair.getKey())
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            this.executorLeaf = mapLeaves.get(branch.getExecutor());
            return this;
        }

        private ContractNode buildNode(IpBasedContract contract){
            return newContractNodeBuilder.apply(new ContractNode(contract));
        }

        private Responsibility buildResponsibility(ContractNode source,
                                                   ContractParticipant participant){
            StakeHolderLeaf target = mapLeaves.get(participant.getStakeholder());
            return new Responsibility(target, source, participant.getPercentage());
        }

        private Responsibility getExecutorResponsibility(Set<Responsibility> responsibilities, StakeHolderLeaf executor) {
            return responsibilities.stream()
                    .filter(responsibility -> {
                        MoneyNode target = responsibility.getTarget();
                        return target instanceof StakeHolderLeaf &&
                                target.getId().equals(executor.getId());
                    })
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No responsibility found targeting executor: " + executor.getId()));
        }

        private ContractNode fullContractNode(IpBasedContract contract){
            ContractNode thisNode = buildNode(contract);
            Set<ContractParticipant> participants = contract.getContractParticipants();
            Set<Responsibility> responsibilities = participants.stream()
                    .map(participant -> buildResponsibility(thisNode, participant))
                    .collect(Collectors.toSet());
            thisNode.setDownStreamEdges(responsibilities);


            return thisNode;
        }

        private List<List<IpBasedContract>> extractNewContractSequences(List<IpBasedContract> remain){
            boolean begin = false;
            int pos = 0;
            List<IpBasedContract> sequence = new ArrayList<>();
            for (IpBasedContract ipBasedContract : remain) {
                pos++;
                if (relationShipsChange.toAddContract.contains(ipBasedContract)) {
                    begin = true;
                    sequence.add(ipBasedContract);
                    continue;
                }
                if (begin) {
                    break;
                }
            }
            List<List<IpBasedContract>> res = new ArrayList<>();
            res.add(sequence);

            if (pos < remain.size()){
                res.add(remain.subList(pos,remain.size()));
                return res;
            }
            res.add(new ArrayList<>());
            return res;
        }

        private List<List<IpBasedContract>> extractNewContractSequences(){
            List<IpBasedContract> searchSpace = new ArrayList<>(branch.getSortedContract());

            List<List<IpBasedContract>> res = new ArrayList<>();
            while (!searchSpace.isEmpty()){
                List<List<IpBasedContract>> splitResult = extractNewContractSequences(searchSpace);
                res.add(splitResult.get(0));
                searchSpace = splitResult.get(1);
            }
            return res.stream().filter(contracts -> !contracts.isEmpty()).toList();
        }

        private void internalSequenceConnect(List<ContractNode> sequencedNode){
            if (sequencedNode.size() == 1){return;}
            Iterator<ContractNode> sources = sequencedNode.subList(0, sequencedNode.size() - 1).iterator();
            Iterator<ContractNode> targets = sequencedNode.subList(1, sequencedNode.size()).iterator();
            while (sources.hasNext() && targets.hasNext()){
                ContractNode source = sources.next();
                ContractNode target = targets.next();
                Responsibility toExecutorLeaf = getExecutorResponsibility(source.getDownStreamEdges(), executorLeaf);
                toExecutorLeaf.setTarget(target);
            }
        }

        private void connectToTargetNode(Map<Integer, ContractNode> nodePriorityMap, Map<IpBasedContract, IpBasedContract> toUpdateTarget ,
                                         Collection<ContractNode> loadedNode){
            Map<ContractNode, ContractNode> toTargetNode = toUpdateTarget.entrySet().stream().collect(Collectors.toMap(
                    entry -> nodePriorityMap.get(entry.getKey().getContractPriority()),
                    entry -> nodePriorityMap.get(entry.getValue().getContractPriority())
            ));

            toTargetNode.forEach((key, value) -> {
                if (loadedNode.contains(key)){
                    key.getDownStreamEdges().forEach(
                            responsibility -> responsibility.setTarget(value)
                    );
                    return;
                }
                getExecutorResponsibility(key.getDownStreamEdges(), executorLeaf).setTarget(value);
            });
        }

        private void connectToSwitchUpperNode(Map<Integer, ContractNode> nodePriorityMap,
                                              Map<IpBasedContract, IpBasedContract> toSwitchUpperEdges ){
            Map<ContractNode, ContractNode> toSwitchUpperNode = toSwitchUpperEdges.entrySet().stream().collect(Collectors.toMap(
                    entry -> nodePriorityMap.get(entry.getKey().getContractPriority()),
                    entry -> nodePriorityMap.get(entry.getValue().getContractPriority())
            ));

            toSwitchUpperNode.forEach((key, value) -> key.getUpStreamEdges().forEach(
                    responsibility -> responsibility.setTarget(value)
            ));
        }

        /* To do:
        *  1. Create a map between IpBasedContract's priority and its contractNode
        *  2. Perform responsibilities swap within the sequence, by query the ContractNode and switch its
        *  Responsibility to point to the next ContractNode in the sequences
        *  3. For each sequence from the toUpdateTarget, figure out if the sequence have any before or after contractNode
        *  4. if they have a before ContractNode, it means they are the last of the batch
        *  -> find the responsibility between the before ContractNode and the executorLeaf and then swap the target to the
        *    ContractNode of the first contract in the sequence
        *  5. if they have an after ContractNode, update their last contractNode to point to the after contract node, find the
        *   upper edges of the afterContractNode and point it to the node of the first contract.
         * */

        private TrackingDetails getTrackingDetails(List<List<IpBasedContract>> newContractSequences) {
            Map<IpBasedContract, IpBasedContract> toUpdateTarget = new HashMap<>();
            Map<IpBasedContract, IpBasedContract> toSwitchUpperEdges = new HashMap<>();
            Set<IpBasedContract> toFetch = new HashSet<>();

            for (List<IpBasedContract> sequence : newContractSequences) {
                if (relationShipsChange.newIsBefore.containsKey(sequence.get(sequence.size() - 1))) {
                    IpBasedContract afterSequenceLastElement = relationShipsChange.newIsBefore
                            .get(sequence.get(sequence.size() - 1));
                    toUpdateTarget.put(sequence.get(sequence.size() - 1), afterSequenceLastElement);
                    toSwitchUpperEdges.put(afterSequenceLastElement, sequence.get(0));
                    toFetch.add(afterSequenceLastElement);
                    continue;
                }

                IpBasedContract beforeSequence = relationShipsChange.getNewIsBefore().entrySet()
                        .stream().filter(pair -> pair.getValue().getContractPriority()
                                .equals(sequence.get(0).getContractPriority()))
                        .map(Map.Entry::getKey).findFirst().orElseThrow();
                toUpdateTarget.put(beforeSequence, sequence.get(0));
                toFetch.add(beforeSequence);
            }
            toFetch.add(relationShipsChange.oldLeadContract);

            return new TrackingDetails(toUpdateTarget, toSwitchUpperEdges, toFetch);
        }

        private record TrackingDetails(Map<IpBasedContract, IpBasedContract> toUpdateTarget,
                                       Map<IpBasedContract, IpBasedContract> toSwitchUpperEdges,
                                       Set<IpBasedContract> toFetch) {}

        private Collection<ContractNode> getLoadedContractNodes(Set<IpBasedContract> toFetch){
            Collection<ContractNode> loadedContractNode = new HashSet<>(contractNodesLoader.apply(toFetch));

            Map<Long, Collection<Responsibility>> upperEdges = upperNodeLoader.apply(loadedContractNode.stream()
                    .map(ContractNode::getId).collect(Collectors.toSet()));

            loadedContractNode.forEach(contractNode ->
                    {
                        if(!upperEdges.containsKey(contractNode.getId())){return;}
                        Collection<Responsibility> currentResponsibilities = upperEdges.get(contractNode.getId());
                        if (currentResponsibilities.isEmpty()){return;}
                        contractNode.setUpStreamEdges(new HashSet<>(currentResponsibilities));
                    });

            return loadedContractNode;
        }

        private void populateField(){
            // Get Sequences of new contracts stand next to each other in merged branch
            List<List<IpBasedContract>> newContractSequences = extractNewContractSequences();

            // Create new contractNode in the system
            Set<ContractNode> affectedContractNodes = relationShipsChange.toAddContract.stream()
                    .map(this::fullContractNode).collect(Collectors.toSet());

            TrackingDetails details = getTrackingDetails(newContractSequences);
            Map<IpBasedContract, IpBasedContract> toUpdateTarget = details.toUpdateTarget;
            Map<IpBasedContract, IpBasedContract> toSwitchUpperEdges = details.toSwitchUpperEdges;
            Set<IpBasedContract> toFetch = details.toFetch;

            Collection<ContractNode> loadedContractNode = getLoadedContractNodes(toFetch);
            affectedContractNodes.addAll(loadedContractNode);

            Map<Integer, ContractNode> nodePriorityMap = affectedContractNodes.stream().collect(Collectors.toMap(
                    node -> node.getContract().getContractPriority(), node -> node
            ));

            updateTail(loadedContractNode, toUpdateTarget);

            internalSequencesConnect(newContractSequences, nodePriorityMap);

            connectToTargetNode(nodePriorityMap, toUpdateTarget, loadedContractNode);
            connectToSwitchUpperNode(nodePriorityMap, toSwitchUpperEdges);

            branchRoot = affectedContractNodes.stream().filter(contractNode ->
                 contractNode.getContract().getContractPriority().equals(
                        relationShipsChange.newLeadContract.getContractPriority()
                )
            ).findFirst().orElseThrow();

            populateContractNodes(affectedContractNodes);
            populateResponsibilities(affectedContractNodes);
            populateSwitchTarget();

        }

        private void updateTail(Collection<ContractNode> loadedContractNode, Map<IpBasedContract, IpBasedContract> toUpdateTarget){
            Optional<ContractNode> oldTail = loadedContractNode.stream().filter(contractNode ->
                    toUpdateTarget.containsKey((IpBasedContract)contractNode.getContract())
            ).findFirst();

            oldTail.ifPresent(tail -> {
                tail.setDownStreamEdges(new HashSet<>());
                Optional<Responsibility> responsibility = tailResponsibilityLoader.apply(tail.getId(), executorLeaf.getId());
                responsibility.ifPresent( res -> {
                    tail.getDownStreamEdges().add(res);
                });
            });
        }

        private void internalSequencesConnect(List<List<IpBasedContract>> newContractSequences, Map<Integer, ContractNode> nodePriorityMap){
            List<List<ContractNode>> sequencesContractNode = newContractSequences.stream().map(
                    sequence -> sequence.stream().map(
                            contract -> nodePriorityMap.get(contract.getContractPriority())
                    ).toList()
            ).toList();

            sequencesContractNode.forEach(this::internalSequenceConnect);
        }

        private void populateSwitchTarget(){
            this.switchTarget = this.responsibilities.stream().filter(
                    responsibility -> responsibility.getTarget() instanceof StakeHolderLeaf
            ).collect(Collectors.toMap(
                    responsibility -> (StakeHolderLeaf) responsibility.getTarget(),
                    responsibility -> responsibility
            ));
        }

        private void populateContractNodes (Set<ContractNode> affectedContractNodes ){
            this.contractNodes = affectedContractNodes.stream().sorted((a, b) -> Integer.compare(
                    b.getContract().getContractPriority(),
                    a.getContract().getContractPriority()
            )).toList();
        }

        private void populateResponsibilities (Set<ContractNode> affectedContractNodes){
            this.responsibilities = affectedContractNodes.stream().map(
                    ContractNode::getDownStreamEdges
            ).filter(Objects::nonNull).flatMap(Set::stream).collect(Collectors.toSet());

            this.responsibilities.addAll(affectedContractNodes.stream().map(
                            ContractNode::getUpStreamEdges).filter(Objects::nonNull).
                    flatMap(Set::stream).collect(Collectors.toSet()));
        }

        public MoneyNodeBranch build(){
            if (executorLeaf == null || newContractNodeBuilder == null || contractNodesLoader == null
                    || tailResponsibilityLoader == null || upperNodeLoader == null){
                throw new IllegalStateException("All the dependencies must be injected before building the MoneyNodeBranch");
            }
            populateField();
            return new MoneyNodeBranch(branch, executorLeaf, switchTarget, branchRoot,
                    contractNodes, responsibilities, mapLeaves);
        }
    }

    public static class Builder{
        private final Branch branch;
        private StakeHolderLeaf executorLeaf;
        private Map<StakeHolderLeaf, Responsibility> switchTarget;
        private ContractNode branchRoot;
        private List<ContractNode> contractNodes;
        private Collection<Responsibility> responsibilities;
        private Map<StakeHolder, StakeHolderLeaf> mapLeaves;

        private Function<ContractNode,ContractNode> newContractNodeBuilder;

        public Builder(Branch branch){
            this.branch = branch;
        }

        public Builder addNewContractNodeBuilder(Function<ContractNode,ContractNode> contractNodeSupplier){
            this.newContractNodeBuilder = contractNodeSupplier;
            return this;
        }

        private void validateContractNodeSupplierInjected(){
            if (!(newContractNodeBuilder == null)){
                return;
            }
            throw new IllegalStateException("The contractNode Supplier must be injected before attempt to build the MoneyNodeBranch");
        }

        public Builder getLeaves(Map<StakeHolder, StakeHolderLeaf> allLeaves) {
            List<StakeHolder> stakeHolders = new java.util.ArrayList<>(branch.getNonExecutorSet().stream().toList());
            stakeHolders.add(branch.getExecutor());
            mapLeaves = allLeaves.entrySet().stream().filter(pair -> stakeHolders.contains(pair.getKey())).collect(
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
            );
            populateExecutorLeaf();
            return this;
        }

        public MoneyNodeBranch build(){
            populateBranch();
            return new MoneyNodeBranch(branch, executorLeaf, switchTarget, branchRoot,
                    contractNodes, responsibilities, mapLeaves);
        }

        private Responsibility buildResponsibility(ContractNode source,
                                                   ContractParticipant participant){
            StakeHolderLeaf target = mapLeaves.get(participant.getStakeholder());
            return new Responsibility(target, source, participant.getPercentage());
        }

        private ContractNode buildNode(IpBasedContract contract){
            return newContractNodeBuilder.apply(new ContractNode(contract));
        }

        private ContractNode fullContractNode(IpBasedContract contract){
            validateContractNodeSupplierInjected();
            ContractNode thisNode = buildNode(contract);
            Set<ContractParticipant> participants = contract.getContractParticipants();
            Set<Responsibility> responsibilities = participants.stream()
                    .map(participant -> buildResponsibility(thisNode, participant))
                    .collect(Collectors.toSet());
            thisNode.setDownStreamEdges(responsibilities);
            return thisNode;
        }

        private void populateExecutorLeaf(){
            validateMapLeavesStaged();
            executorLeaf = mapLeaves.get(branch.getExecutor());
        }

        private void validateMapLeavesStaged(){
            if (mapLeaves == null){
                throw new IllegalStateException("The leaves must be filled in before populating the branch");
            }
        }

        private void updatesTheTarget(ContractNode source, ContractNode target){
            Responsibility toExecutor = source.getDownStreamEdges().stream().filter(
                    responsibility -> responsibility.getTarget().equals(executorLeaf)
            ).findFirst().orElseThrow(
                    () -> new NullPointerException("The responsibility point to this executor is not exist")
            );
            toExecutor.setTarget(target);
        }

        private void updateTheTargetOfTheBranch(List<ContractNode> addedDownstreamNodes){
            Iterator<ContractNode> sources = addedDownstreamNodes.subList(0,addedDownstreamNodes.size() - 1).iterator();
            Iterator<ContractNode> targets = addedDownstreamNodes.subList(1, addedDownstreamNodes.size()).iterator();
            while(sources.hasNext() && targets.hasNext()){
                ContractNode source = sources.next();
                ContractNode target = targets.next();
                updatesTheTarget(source,target);
            }
        }

        private void populateBranch(){
            validateMapLeavesStaged();
            List<IpBasedContract> contracts = branch.getSortedContract();
            List<ContractNode> addedDownstreamNodes = contracts.stream().map(this::fullContractNode).toList();

            this.branchRoot = addedDownstreamNodes.get(0);
            this.contractNodes = addedDownstreamNodes;
            this.responsibilities = addedDownstreamNodes.stream().map(ContractNode::getDownStreamEdges)
                    .flatMap(Set::stream).collect(Collectors.toSet());

            if (contractNodes.size() > 1){
                updateTheTargetOfTheBranch(addedDownstreamNodes);
            }

            this.switchTarget = responsibilities.stream()
                    .filter(responsibility -> responsibility.getTarget() instanceof StakeHolderLeaf)
                    .collect(
                            Collectors.toMap(responsibility -> (StakeHolderLeaf) responsibility.getTarget()
                                    ,responsibility -> responsibility)
                    );
        }
    }
}
