package com.LegalEntitiesManagement.v1.Entities.services;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractNodeNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractViolatedException.ContractValidationFailed;
import com.LegalEntitiesManagement.v1.Entities.exceptions.IpTreeNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.*;
import com.LegalEntitiesManagement.v1.Entities.model.supportClass.*;
import com.LegalEntitiesManagement.v1.Entities.model.supportClass.MoneyNodeBranch.*;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.*;

import java.util.*;
import java.util.stream.Collectors;
public class GraphBuilderService {
    final IpTreeService ipTreeService;
    final ContractNodeService contractNodeService;
    final StakeHolderLeafService stakeHolderLeafService;
    final ResponsibilityService responsibilityService;

    public GraphBuilderService(IpTreeService ipTreeService, ContractNodeService contractNodeService,
                               StakeHolderLeafService stakeHolderLeafService,
                               ResponsibilityService responsibilityService) {
        this.ipTreeService = ipTreeService;
        this.contractNodeService = contractNodeService;
        this.stakeHolderLeafService = stakeHolderLeafService;
        this.responsibilityService = responsibilityService;
    }

    /*
     * Step to build new Trees:
     * - 1. Group IpBasedContracts by its executor -> create branch from groups
     * - 1.1 Validate the branches by make sure the same stakeHolder do not appear twice on
     * - 2. Sort the executors of each group by priority in descending order
     * - 3. Iterate through each branch and collect their non executor stakeholder
     * - 4. The non executor stakeholder collections must be not overlap between branches
     * - 5. Iterate through each branch and build the branch with node and responsibilities that branch present
     * - 6. Each branch is a mini tree build from same executor contract nodes
     * - 7. We connect branch's contract nodes by replace the target of the responsibilities of the higher
     * branch with its own executor's StakeHolderLeaf by the next ContractNode.
     * - 8. We come to the top branch (the one with the executor that have role priority)
     * - 9. Iterate through its responsibility and replace the non executor target StakeholderLeaf with the
     * head node of the branch that have the same executor.
     * */


    // Group contracts by executor, and build branch on each groups
    private Map<StakeHolder, List<IpBasedContract>> groupContractByExecutor(Collection<IpBasedContract> contracts){
        return contracts.stream().collect(Collectors.groupingBy(
                Contract::getExecutor
        ));
    }

    private List<Branch> getBranches(Collection<IpBasedContract> contracts){
        Map<StakeHolder, List<IpBasedContract>> contractsGroups = groupContractByExecutor(contracts);
        return contractsGroups.values().stream().map(Branch::new).sorted(
                Comparator.comparingInt(branch -> - branch.getExecutor().getRole().getPriority())
        ).toList();
    }

    // perform cross branches validation
    private Set<StakeHolder> overSteppedExecutor(Collection<StakeHolder> executors, Branch branch){
        Set<StakeHolder> examined = branch.getExamined(executors);
        return examined.stream().filter(branch::isHigherEqualExecutor).collect(Collectors.toSet());
    }

    // validate a target executors must have lower priority than source
    private void validateBranchesPriorityWithSingleBranch(Collection<StakeHolder> executors, Branch branch){
        Set<StakeHolder> overSteppedStakeHolder = overSteppedExecutor(executors, branch);
        if (overSteppedStakeHolder.isEmpty()){
            return;
        }

        StringBuilder messageBuilder = new StringBuilder(String
                .format("The branch with executor's id: %s having higher priority executors as its target: \n",
                        branch.getExecutor().getId())
        );

        overSteppedStakeHolder.forEach(stakeHolder -> {
            String detail = String.format("This executor with id: %s have been overstepped \n", stakeHolder.getId());
            messageBuilder.append(detail);
        });

        throw new ContractValidationFailed(messageBuilder.toString());
    }

    private void validateBranchesPriority(Collection<StakeHolder> executors, Collection<Branch> branches){
        branches.forEach(branch -> validateBranchesPriorityWithSingleBranch(executors, branch));
    }

    // validate across branch no non executor target is overlap
    private void validateNonExecutorOverLapping(List<Set<StakeHolder>> nonExecutorSets){
        if (!utilClass.noOverlapNonExecutorStakeHolder(nonExecutorSets)) {
            Set<StakeHolder> overlapping = utilClass.findOverlapping(nonExecutorSets);
            throw new ContractValidationFailed("Stakeholders appear in multiple branch: " +
                    overlapping.stream().map(StakeHolder::getName).collect(Collectors.joining(", ")));
        }
    }

    // validate all branch connect to each other
    private boolean isConnect(Branch branch1, Branch branch2){
        return branch1.haveTarget(branch2) || branch2.haveTarget(branch1);
    }

    private Set<Branch> extractConnectedBranches(Collection<Branch> branches){
        List<Branch> branchesList = new ArrayList<>(branches);

        List<Branch> connectedGroup = branchesList.subList(0,1);
        Set<Branch> searchGroup = new HashSet<>(branchesList.subList(1, branches.size()));
        int pos = 0;

        while (pos < connectedGroup.size() && !searchGroup.isEmpty()){
            Branch currentBranch = connectedGroup.get(pos);
            pos ++;
            Set<Branch> toAdd = new HashSet<>();
            searchGroup.forEach(branch -> {
                if (isConnect(branch, currentBranch)){
                    toAdd.add(branch);
                }
            });
            searchGroup.removeAll(toAdd);
            connectedGroup.addAll(toAdd);
        }
        return new HashSet<>(connectedGroup);
    }

    private List<Set<Branch>> extractAllIsolatedBranchesGroup(Collection<Branch> branches){
        List<Set<Branch>> allIsolatedBranchesGroup = new ArrayList<>();
        Set<Branch> toExtract = new HashSet<>(branches);
        while(!toExtract.isEmpty()){
            Set<Branch> extracted = extractConnectedBranches(toExtract);
            allIsolatedBranchesGroup.add(extracted);
            toExtract.removeAll(extracted);
        }

        return allIsolatedBranchesGroup;
    }

    private boolean haveMultipleIsolatedGroup(Collection<Branch> branches){
        List<Set<Branch>> isolatedBranchesGroups = extractAllIsolatedBranchesGroup(branches);
        return isolatedBranchesGroups.size() > 1;
    }

    private void validateConnectedBranches(Collection<Branch> branches){
        if (haveMultipleIsolatedGroup(branches)){
            throw new ContractValidationFailed("The branches do not connect to each other");
        }
    }

    private void crossBranchesValidation(List<Branch> branches){
        List<StakeHolder> executors = branches.stream().map(Branch::getExecutor).toList();
        List<Set<StakeHolder>> nonExecutorSets = branches.stream().map(Branch::getNonExecutorSet).toList();
        validateNonExecutorOverLapping(nonExecutorSets);
        validateBranchesPriority(executors, branches);
        validateConnectedBranches(branches);
    }

    private Map<StakeHolder, StakeHolderLeaf> getLeafDependencies(Collection<Branch> branches){
        Set<StakeHolder> executors = branches.stream().map(Branch::getExecutor).collect(Collectors.toSet());
        Set<StakeHolder> allStakeHolder = branches.stream().map(Branch::getNonExecutorSet).flatMap(Set::stream).collect(Collectors.toSet());
        allStakeHolder.addAll(executors);
        return stakeHolderLeafService.getLeaves(allStakeHolder);
    }

    private List<MoneyNodeBranch> buildMoneyNodeBranches(List<Branch> branches){
        Map<StakeHolder, StakeHolderLeaf> leavesMap = getLeafDependencies(branches);
        return branches.stream().map(branch -> {
            Builder builder = new Builder(branch);
            return builder.addNewContractNodeBuilder(contractNodeService::save).getLeaves(leavesMap).build();
        }).toList();
    }

    private void connectNewBuiltBranch(List<MoneyNodeBranch> moneyNodeBranches){
        Set<MoneyNodeBranch> toConnectUpstreamBranches = new HashSet<>(moneyNodeBranches.subList(1, moneyNodeBranches.size()));
        Set<MoneyNodeBranch> inQueueBranches = new HashSet<>();
        inQueueBranches.add(moneyNodeBranches.get(0));

        while(!toConnectUpstreamBranches.isEmpty() && inQueueBranches.size() < moneyNodeBranches.size()){
            MoneyNodeBranch currentTopBranch = inQueueBranches.stream().findFirst().orElseThrow();
            Set<MoneyNodeBranch> toAdd = new HashSet<>();
            toConnectUpstreamBranches.forEach(moneyNodeBranch -> {
                if (currentTopBranch.containTarget(moneyNodeBranch)){
                    currentTopBranch.switchTarget(moneyNodeBranch);
                    toAdd.add(moneyNodeBranch);
                }
            });

            toConnectUpstreamBranches.removeAll(toAdd);
            inQueueBranches.addAll(toAdd);
            inQueueBranches.remove(currentTopBranch);
        }
    }

    public void validateBuildNewTree(List<IpBasedContract> contracts){
        List<Branch> branches = getBranches(contracts);
        crossBranchesValidation(branches);
    }

    public IpTree buildNewTree(List<IpBasedContract> contracts){
        List<Branch> branches = getBranches(contracts);
        List<MoneyNodeBranch> moneyNodeBranches = buildMoneyNodeBranches(branches);
        connectNewBuiltBranch(moneyNodeBranches);

        // Persist all responsibilities
        Set<Responsibility> allResponsibilities = moneyNodeBranches.stream()
                .map(MoneyNodeBranch::getResponsibilities)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        responsibilityService.saveAll(new ArrayList<>(allResponsibilities));

        // Saved the tree
        ContractNode rootNode = moneyNodeBranches.get(0).getBranchRoot();
        IpTree newIpTree = new IpTree();
        newIpTree.setIntellectualProperty(contracts.get(0).getIntellectualProperty());
        newIpTree.setRootContractNode(rootNode);
        return ipTreeService.save(newIpTree);
    }

    /*
    * Step to validate when to delete a contract
    * 1 it is valid if it is the lowest level contractNode (all responsibilities target being stakeholderLeaf)
    * 2 it is valid if it is belonged to a multiNode branch, and its downStreamEdges do not have any other contractNode
    * branch as it downstream node
    * */

    private ContractNode getFullContractNode(IpBasedContract contract){
        ContractNode contractNode = contractNodeService.findByContractId(contract.getId())
                .orElseThrow(() -> new ContractValidationFailed("The contract attempt to delete do not have a registered ContractNode"));
        Set<Responsibility> responsibilities = responsibilityService.findDownstreamEdges(contractNode.getId());
        contractNode.setDownStreamEdges(responsibilities);
        return contractNode;
    }

    private boolean canDeleteContractNode(IpBasedContract contract){
        ContractNode contractNode = getFullContractNode(contract);
        return hasNoDownStreamContractNode(contractNode) || downStreamContractNodeInTheSameBranch(contractNode);
    }

    private boolean hasNoDownStreamContractNode(ContractNode contractNode){
        Set<Responsibility> responsibilities = contractNode.getDownStreamEdges();
        return responsibilities.stream().map(Responsibility::getTarget)
                .noneMatch(moneyNode -> moneyNode instanceof ContractNode);
    }

    private Set<ContractNode> getAllDownStreamContractNode(ContractNode contractNode){
        Set<Responsibility> responsibilities = contractNode.getDownStreamEdges();
        return responsibilities.stream().map(Responsibility::getTarget)
                .filter(moneyNode -> moneyNode instanceof ContractNode).map(moneyNode -> (ContractNode) moneyNode)
                .collect(Collectors.toSet());
    }

    private boolean downStreamContractNodeInTheSameBranch(ContractNode currentContractNode){
        Set<ContractNode> allDownStreamContractNode = getAllDownStreamContractNode(currentContractNode);
        if (allDownStreamContractNode == null){
            return true;
        }
        if (allDownStreamContractNode.isEmpty()){
            return true;
        }
        if (allDownStreamContractNode.size() > 1){
            return false;
        }
        return allDownStreamContractNode.stream().allMatch(
                contractNode -> contractNode.getContract().getExecutor().equals(
                        currentContractNode.getContract().getExecutor()
                )
        );
    }

    public void validateDeleteContract(IpBasedContract contract){
        if(canDeleteContractNode(contract)){
            return;
        }
        throw new ContractValidationFailed("Can not delete current contract, check if they have downstream contract with different executor");
    }

    private boolean isIpTreeRoot (ContractNode contractNode, IpTree ipTree){
        return ipTree.getRootContractNode().getId().equals(contractNode.getId());
    }

    private void switchTarget(Responsibility responsibility, MoneyNode target){
        responsibility.setTarget(target);
        responsibilityService.save(responsibility);
    }

    private void deleteContractNode(ContractNode contractNode, Set<Responsibility> downStreamEdges){
        contractNodeService.deleteById(contractNode.getId());
        responsibilityService.deleteAll(downStreamEdges.stream().toList());
    }

    public void updateDeleteContract(IpBasedContract contract){
        // declare information
        ContractNode toDeleteContractNode = getFullContractNode(contract);
        Set<Responsibility> downStreamEdges = responsibilityService.findDownstreamEdges(toDeleteContractNode.getId());
        Set<ContractNode> downStreamContractNodes = getAllDownStreamContractNode(toDeleteContractNode);
        Optional<ContractNode> newTargetNode = downStreamContractNodes.stream().findFirst();
        IpTree ipTree = ipTreeService.findByIntellectualPropertyId(contract.getIntellectualProperty().getId())
                .orElseThrow(() -> new IpTreeNotFoundException("intellectual property",
                        contract.getIntellectualProperty().getId()));

        if (isIpTreeRoot(toDeleteContractNode, ipTree)){
            deleteContractNode(toDeleteContractNode, downStreamEdges);
            ipTree.setRootContractNode(newTargetNode.orElse(null));
            ipTreeService.save(ipTree);
            return;
        }

        StakeHolderLeaf toDeleteContractNodeExecutorLeaf = stakeHolderLeafService.findByStakeholderId(
                toDeleteContractNode.getContract().getExecutor().getId()
        );

        Set<Responsibility> upstreamEdges = responsibilityService.findUpstreamEdges(toDeleteContractNode.getId());
        Optional<Responsibility> upstreamEdge = upstreamEdges.stream().findFirst();
        MoneyNode target = newTargetNode.isPresent() ? newTargetNode.get() : toDeleteContractNodeExecutorLeaf;
        upstreamEdge.ifPresent(responsibility -> switchTarget(responsibility, target));
        deleteContractNode(toDeleteContractNode, downStreamEdges);
    }

    /*
     * Step to validate update a contracts
     * 1. Contract must not have new executor
     * 2. Contract's new participants must not:
     *    - Belong to any branch's non-executor stakeholders
     *    - Be an executor in any contract
     * 3. If priority changed:
     *    - Validate new priority not duplicate with other priority
     */

    private boolean haveSameExecutor(IpBasedContract newContract, IpBasedContract oldContract){
        return newContract.getExecutor().equals(oldContract.getExecutor());
    }

    private void validateNoChangeExecutor(IpBasedContract newContract, IpBasedContract oldContract){
        if (haveSameExecutor(newContract, oldContract)){return;}
        throw new ContractValidationFailed("The update of contract executor is forbidden");
    }

    private Set<StakeHolder> diffStakeHolder(IpBasedContract contract1, IpBasedContract contract2){
        Set<StakeHolder> firstContractStakeHolder = contract1.getContractParticipants().stream()
                .map(ContractParticipant::getStakeholder).collect(Collectors.toSet());
        Set<StakeHolder> secondContractStakeHolder = contract2.getContractParticipants().stream()
                .map(ContractParticipant::getStakeholder).collect(Collectors.toSet());
        secondContractStakeHolder.retainAll(firstContractStakeHolder);
        firstContractStakeHolder.removeAll(secondContractStakeHolder);
        return firstContractStakeHolder;
    }

    private boolean violateUpdatePriorityUniqueness(IpBasedContract newContract, IpBasedContract oldContract, Branch currentBranch){
        if (oldContract.getContractPriority().equals(newContract.getContractPriority())){return false;}
        Set<IpBasedContract> diffContract = new HashSet<>(currentBranch.getContracts());
        diffContract.remove(oldContract);
        return diffContract.stream().anyMatch(
                contract -> contract.getContractPriority().equals(newContract.getContractPriority()));
    }

    private void validateUpdatePriorityUniqueness(IpBasedContract newContract, IpBasedContract oldContract, Branch branch){
        if (!violateUpdatePriorityUniqueness(newContract, oldContract, branch)){return;}
        throw new ContractValidationFailed("The attempted updated contract have the same priority with other on the same branch");
    }

    private boolean violateUpdateUniqueNonExecutor(Branch currentBranch, Set<StakeHolder> newStakeHolders) {
        return newStakeHolders.isEmpty() || newStakeHolders.stream()
                .anyMatch(stakeHolder -> currentBranch.getNonExecutorSet().contains(stakeHolder));
    }

    private void validateUpdateUniqueNonExecutor(Branch currentBranch, Set<StakeHolder> newStakeHolders){
        if (!violateUpdateUniqueNonExecutor(currentBranch, newStakeHolders)){return;}
        throw new ContractValidationFailed("The attempted updated contract have overlap non-executor stakeholder with other on the same branch");
    }

    private boolean isBelongToTree(IpBasedContract newContract, List<IpBasedContract> treeContracts){
        return treeContracts.stream().map(IpBasedContract::getId).collect(Collectors.toSet()).contains(newContract.getId());
    }

    private void validateContractIsBelongToTree(IpBasedContract newContract, List<IpBasedContract> treeContracts){
        if (!isBelongToTree(newContract, treeContracts)){
            throw  new ContractValidationFailed("The Ip based contract attempted to update to not belong to any tree");
        }
    }

    private boolean affectDownStreamConnection(Set<StakeHolder> affectedStakeHolder, List<Branch> otherBranches){
        Set<StakeHolder> executors = otherBranches.stream().map(Branch::getExecutor).collect(Collectors.toSet());
        return executors.stream().anyMatch(affectedStakeHolder::contains);
    }

    private void validateAffectedDownStreamConnection(Set<StakeHolder> affectedStakeHolder, List<Branch> otherBranches){
        if(!affectDownStreamConnection(affectedStakeHolder, otherBranches)){return;}
        throw new ContractValidationFailed(
                String.format("At least one of the attempted %s contract's affected participant is executor of other contracts branch", "update")
        );
    }

    private boolean containDuplicatedNonExecutor(Set<StakeHolder> affectedStakeHolder, List<Branch> otherBranches){
        Set<StakeHolder> otherNonExecutor = otherBranches.stream().map(Branch::getNonExecutorSet).flatMap(Set::stream)
                .collect(Collectors.toSet());
        return otherNonExecutor.stream().anyMatch(affectedStakeHolder::contains);
    }

    private void validateDuplicatedNonExecutor(Set<StakeHolder> affectedStakeHolder, List<Branch> otherBranches){
        if (!containDuplicatedNonExecutor(affectedStakeHolder, otherBranches)){return;}
        throw new ContractValidationFailed(
                String.format("The attempted %s contract create duplicated non-executors with other branches", "update")
        );
    }

    public void validateUpdateContract(IpBasedContract newContract, List<IpBasedContract> treeContracts){
        validateContractIsBelongToTree(newContract, treeContracts);
        IpBasedContract oldContract = treeContracts.stream().filter(
                contract -> contract.getId().equals(newContract.getId())
        ).findFirst().orElseThrow();
        validateNoChangeExecutor(newContract, oldContract);

        // validate internal branch
        Set<StakeHolder> newContractParticipant = diffStakeHolder(newContract, oldContract);
        List<Branch> branches = getBranches(treeContracts);
        Branch contractBranch = branches.stream().filter(branch -> branch.getContracts().contains(oldContract))
                .findFirst().orElseThrow();
        validateUpdatePriorityUniqueness(newContract, oldContract, contractBranch);
        validateUpdateUniqueNonExecutor(contractBranch, newContractParticipant);

        // validate cross branch
        Set<StakeHolder>  affectedStakeHolder = diffStakeHolder(oldContract, newContract);
        affectedStakeHolder.addAll(newContractParticipant);
        List<Branch> otherBranches =  branches.stream().filter(
                branch -> !branch.getExecutor().equals(newContract.getExecutor())
        ).toList();
        validateAffectedDownStreamConnection(affectedStakeHolder, otherBranches);
        validateDuplicatedNonExecutor(affectedStakeHolder, otherBranches);
    }

    /*
    * Step to update contractNodes
    * 1. if there is no affected stakeHolder, look through responsibility map them with participants and update the
    *    percentage accordingly
    * 2. if there is stakeHolder to be removed, find the responsibility of those stakeholder and removed them
    *    if there is stakeHolder to be added, create new responsibility between the contractNode and those stakeHolderLeaf
    *    saved those newly created responsibilities
    * 3. if the priority change lead to a change in the order of the branch
    *    reshuffle the branch and updated the responsibilities between those affected node accordingly
    * */

    private Branch updateBranch(IpBasedContract newContract, IpBasedContract oldContract, Branch branch){
        Set<IpBasedContract> contracts = new HashSet<>(branch.getContracts());
        contracts.remove(oldContract);
        contracts.add(newContract);
        return new Branch(contracts);
    }

    private MoneyNode getMoneyNodeOfBranch(int index, Branch branch){
        List<IpBasedContract> contracts = branch.getSortedContract();
        if (index < 0){
            return null;
        } // if negative index then it mean the contract is the root of the branch

        if (index == contracts.size()){ // if == the size then it mean the contract is the tail of the branch
            return stakeHolderLeafService.findByStakeholderId(contracts.get(0).getExecutor().getId());
        }

        return contractNodeService.findByContractId(contracts.get(index).getId()).orElseThrow(() -> new ContractNodeNotFoundException(
                String.format("Contract node associate with contract with id: %s is not found", contracts.get(index).getId()))
        );
    }

    private Set<Responsibility> getNewUpStreamEdges(MoneyNode newTarget, MoneyNode newSource){
        Set<Responsibility> newUpperEdges = new HashSet<>();
        if (newTarget instanceof StakeHolderLeaf){

            newUpperEdges.add(responsibilityService.findBySourceAndTarget(
                    Objects.requireNonNull(newSource).getId(), Objects.requireNonNull(newTarget).getId()).orElseThrow());
            return newUpperEdges;
        }
        newUpperEdges.addAll(responsibilityService.findUpstreamEdges(newTarget.getId()));
        return newUpperEdges;
    }

    private Set<Responsibility> reOrderBranch(ContractNode contractNode, IpBasedContract newContract, IpBasedContract oldContract,
                                              Branch newBranch, Branch oldBranch, int branchIndex){
        Set<Responsibility> allChanges = new HashSet<>();
        int oldIndex = oldBranch.getSortedContract().indexOf(oldContract);
        int newIndex = newBranch.getSortedContract().indexOf(newContract);
        if (oldIndex == newIndex){return allChanges;}

        /*
        * 1. If branch is top of the tree, branchIndex = 0 add these logic
        * -> oldSource = null -> used to be head of the tree -> oldTarget is the newHead
        * -> newSource = null -> become the new head of the tree
        * 2. Usually
        * current upper edge point to old target - done
        * current node point to new target - done
        * new target upper edge  point to current node
        * (if new target is StakeHolderLeaf find the new target upper edge by using the findBySourceAndTarget of responsibilityService)
        * else use findUpstreamEdges and used for each;
        * */
        MoneyNode oldSource = getMoneyNodeOfBranch(oldIndex - 1, oldBranch);
        MoneyNode oldTarget = getMoneyNodeOfBranch(oldIndex + 1, oldBranch);
        MoneyNode newSource = getMoneyNodeOfBranch(newIndex - 1, newBranch);
        MoneyNode newTarget = getMoneyNodeOfBranch(newIndex + 1, newBranch);
        Set<Responsibility> upStreamEdges = responsibilityService.findUpstreamEdges(contractNode.getId());

        Responsibility toReWireDownStreamEdge = responsibilityService.findBySourceAndTarget(
                contractNode.getId(), Objects.requireNonNull(oldTarget).getId()).orElseThrow();

        switchTarget(toReWireDownStreamEdge, newTarget);
        upStreamEdges.forEach(edge -> switchTarget(edge, oldTarget));
        allChanges.addAll(upStreamEdges);
        Set<Responsibility> newUpperEdges = getNewUpStreamEdges(newTarget, newSource);
        newUpperEdges.forEach(edge -> switchTarget(edge, contractNode));
        allChanges.addAll(newUpperEdges);
        if (branchIndex != 0){return allChanges;}
        IpTree currentTree = ipTreeService.findByIntellectualPropertyId(newContract.getIntellectualProperty().getId())
                .orElseThrow(() -> new IpTreeNotFoundException("Intellectual property", newContract.getIntellectualProperty().getId()));

        if (oldSource == null){
            currentTree.setRootContractNode((ContractNode) oldTarget);
        }

        if (newSource == null) {
            currentTree.setRootContractNode(contractNode);
        }

        ipTreeService.save(currentTree);

        return allChanges;
    }
    private Set<Responsibility> updatedPercentageResponsibilities(Set<ContractParticipant> participants, Set<Responsibility> responsibilities){

        Map<StakeHolder, ContractParticipant> participantMap = participants.stream().collect(Collectors.toMap(
                ContractParticipant::getStakeholder, participant -> participant
        ));

        Set<Responsibility> toLeafResponsibility = responsibilities.stream().filter(
                responsibility -> responsibility.getTarget() instanceof StakeHolderLeaf
        ).collect(Collectors.toSet());

        Set<Responsibility> toContractResponsibility = responsibilities.stream().filter(
                responsibility -> responsibility.getTarget() instanceof ContractNode
        ).collect(Collectors.toSet());

        toLeafResponsibility.forEach(responsibility -> {
            StakeHolder stakeHolder = ((StakeHolderLeaf) responsibility.getTarget()).getStakeHolder();
            if (participantMap.containsKey(stakeHolder)){
                responsibility.setPercentage(participantMap.get(stakeHolder).getPercentage());
            }
        });

        toContractResponsibility.forEach(
                responsibility -> {
                    StakeHolder stakeHolder = ((ContractNode) responsibility.getTarget()).getContract().getExecutor();
                    if (participantMap.containsKey(stakeHolder)){
                        responsibility.setPercentage(participantMap.get(stakeHolder).getPercentage());
                    }
                }
        );

        return responsibilities;
    }
    private Set<Responsibility> newNodeResponsibilities(ContractNode contractNode, IpBasedContract newContract, IpBasedContract oldContract){
        Set<Responsibility> currentDownStreamEdges = contractNode.getDownStreamEdges();

        // remove to delete responsibilities
        Set<StakeHolder> toRemoveParticipant = diffStakeHolder(oldContract, newContract);
        Set<Responsibility> downStreamLeafEdges = currentDownStreamEdges.stream().filter(
                edge -> edge.getTarget() instanceof StakeHolderLeaf
        ).collect(Collectors.toSet());
        List<Responsibility> toDeleteResponsibilities = downStreamLeafEdges.stream().filter(
                edge -> toRemoveParticipant.contains( ((StakeHolderLeaf)edge.getTarget()).getStakeHolder() )
        ).toList();
        responsibilityService.deleteAll(toDeleteResponsibilities);

        // update remain responsibilities
        currentDownStreamEdges.removeAll(new HashSet<>(toDeleteResponsibilities));

        Set<StakeHolder> toAddParticipants = diffStakeHolder(newContract, oldContract);
        Map<StakeHolder, StakeHolderLeaf> leavesMap = stakeHolderLeafService.getLeaves(toAddParticipants);
        currentDownStreamEdges.addAll(toAddParticipants.stream().map(
                participant -> new Responsibility(leavesMap.get(participant), contractNode, 0)
                ).collect(Collectors.toSet())
        );
        return updatedPercentageResponsibilities(newContract.getContractParticipants(), currentDownStreamEdges);
    }

    public void updateContract(IpBasedContract newContract, List<IpBasedContract> treeContracts){
        List<Branch> branches = getBranches(treeContracts);
        Branch contractBranch = branches.stream().filter(
                branch -> branch.getExecutor().equals(newContract.getExecutor())
        ).findFirst().orElseThrow();
        int branchIndex = branches.indexOf(contractBranch);
        IpBasedContract oldContract = contractBranch.getSortedContract().stream().filter(
                contract -> contract.getId().equals(newContract.getId())
        ).findFirst().orElseThrow();
        Branch updatedBranch = updateBranch(newContract, oldContract, contractBranch);
        ContractNode currentContractNode = getFullContractNode(newContract);
        Set<Responsibility> toUpdate = reOrderBranch(currentContractNode, newContract, oldContract, updatedBranch, contractBranch, branchIndex);
        toUpdate.addAll(newNodeResponsibilities(currentContractNode, newContract, oldContract));
        responsibilityService.saveAll(toUpdate);
    }

    /*Step to validate add new Contract to alreadyExisted Tree
    * -> 1. Get the original trees contracts + Get the new batch of contracts
    * -> 2. Transform them into branches group
    * -> 3. Clustered branches in the new batch into connected group
    * -> 4. Verify each branch groups of the new branch to the centered group:
    *  - If there is two executors in the original tree in a new branch group -> failed
    *  - first condition: If the top branch this group is not belong to any non executor leaf, or branches of original tree
    *  - second condition: If the root of the tree do not belong to any non executor leaf of the new branch
    *  - if first and second condition all together -> failed, no connection with original tree
    *  - otherwise, check if the non executor leaf of two group overlap,
    *  - check if the branch of new group and old group with identical executor having conflict when merged, check
    *   contract priority only.
    * */

    private boolean violateOverlapTargetStakeHolder(Set<StakeHolder> newNonExecutorStakeHolder, Set<StakeHolder> oldNonExecutorsStakeHolder){
        Set<StakeHolder> temp = new HashSet<>(newNonExecutorStakeHolder);
        temp.retainAll(oldNonExecutorsStakeHolder);
        return !temp.isEmpty();
    }

    private boolean violateNoConnection(Set<StakeHolder> newNonExecutorStakeHolder, Set<StakeHolder> oldNonExecutorsStakeHolder,
                                       StakeHolder newBranchesTopExecutor, StakeHolder oldBranchesTopExecutor){
        return !newNonExecutorStakeHolder.contains(oldBranchesTopExecutor)
                && !oldNonExecutorsStakeHolder.contains(newBranchesTopExecutor);
    }

    private boolean violateMergeBranch(Branch souceMergeBranch, Collection<Branch> targetMergeBranches,
                                       Collection<StakeHolder> executorOfBranchesToMerge){

        if (!executorOfBranchesToMerge.contains(souceMergeBranch.getExecutor())){
            // if no overlap executors, then no need to perform validation since it mean that it will not have overlap branch to merge
            return false;
        }

        Branch matchBranch = targetMergeBranches.stream().filter(
                branch -> branch.getExecutor().equals(souceMergeBranch.getExecutor())
        ).findFirst().orElseThrow();

        Set<Integer> sourceMergeBranchPriority = souceMergeBranch.getContracts().stream().map(
                Contract::getContractPriority
        ).collect(Collectors.toSet());

        Set<Integer> matchBranchPriority = matchBranch.getContracts().stream().map(
                Contract::getContractPriority
        ).collect(Collectors.toSet());

        sourceMergeBranchPriority.retainAll(matchBranchPriority);

        return !sourceMergeBranchPriority.isEmpty();
    }

    private void validateSingleGroupToTree(Collection<Branch> newBranches, Collection<Branch> oldBranches){
        Set<StakeHolder> newNonExecutorStakeHolder = newBranches.stream().map(Branch::getNonExecutorSet)
                .flatMap(Set::stream).collect(Collectors.toSet());

        Set<StakeHolder> oldNonExecutorsStakeHolder = oldBranches.stream().map(Branch::getNonExecutorSet)
                .flatMap(Set::stream).collect(Collectors.toSet());

        Branch topNewBranch = newBranches.stream().sorted(
                Comparator.comparingInt(b -> -b.getExecutor().getRole().getPriority())
        ).toList().get(0);

        StakeHolder newBranchesTopExecutor = topNewBranch.getExecutor();

        Branch topOldBranch = oldBranches.stream().sorted(
                Comparator.comparingInt(b -> -b.getExecutor().getRole().getPriority())
        ).toList().get(0);

        StakeHolder oldBranchesTopExecutor = topOldBranch.getExecutor();
        if (violateOverlapTargetStakeHolder(newNonExecutorStakeHolder, oldNonExecutorsStakeHolder)){
            throw new ContractValidationFailed("The new batch of contracts violate rule of adding middle contractNode on the tree as target");
        }

        if (violateNoConnection(newNonExecutorStakeHolder, oldNonExecutorsStakeHolder, newBranchesTopExecutor, oldBranchesTopExecutor)){
            throw new ContractValidationFailed("The new batch of contracts having group of connected branches that " +
                    "do not connect to the original tree");
        }

        Set<StakeHolder> newBranchExecutors = newBranches.stream().map(Branch::getExecutor).collect(Collectors.toSet());
        Set<StakeHolder> oldBranchExecutors = oldBranches.stream().map(Branch::getExecutor).collect(Collectors.toSet());
        if (violateMergeBranch(topOldBranch, newBranches, newBranchExecutors)
                || violateMergeBranch(topNewBranch, oldBranches, oldBranchExecutors)){
            throw new ContractValidationFailed("The new batch of contracts create merge branch that violate contract priority uniqueness");
        }
    }

    private void validateAllGroupToTree(List<Set<Branch>> connectedGroupsNewBranch, List<Branch> currentBranches){
        connectedGroupsNewBranch.forEach(branches -> validateSingleGroupToTree(branches, currentBranches));
    }

    public void validateAddNewContractToExistedTree(List<IpBasedContract> newContracts, List<IpBasedContract> currentTreeContracts){
        List<Branch> newContractsBranches = getBranches(newContracts);
        List<Branch> currentBranches = getBranches(currentTreeContracts);
        List<Set<Branch>> connectedGroupsNewBranch = extractAllIsolatedBranchesGroup(newContractsBranches);
        validateAllGroupToTree(connectedGroupsNewBranch, currentBranches);
    }

    /*
     * Step to add the branches (all branch here refer to new branch)
     *  - if the top branch is into non-executor leaf, build the branches and set the responsibility point to the
     *   non executor leaf to the head of the branch
     *  - if the root of the tree belong to the non executor leaf of the branch, connect them together by switch  as usual,
     *   Set the new tree root
     *  - if the branch is about to merge to other branch of the original tree, perform the order set like the update priority cases
     *  - then merge other, merge them all, one by one, saved the newly created contractNode one by one, save responsibility in batch
     * */

    /* Steps to connect single new branches group to the old branches
    *  Divide to 3 cases:
    * -> top new branches executor is not belong to currentBranches executor but also belong to their non-executor
    * -> top currentBranches executor is not belong to old branches executor but also belong to their non-executor
    * -> top new branches is linked to one of currentBranches
    * */

    private boolean isLeaf(StakeHolder topAdditionalExecutor, List<Branch> tree){
        Set<StakeHolder> treeExecutors = tree.stream().map(Branch::getExecutor).collect(Collectors.toSet());
        Set<StakeHolder> treeLeaves = tree.stream().map(Branch::getNonExecutorSet).flatMap(Set::stream)
                .collect(Collectors.toSet());
        treeExecutors.retainAll(treeLeaves);
        if (treeExecutors.contains(topAdditionalExecutor)){return false;}
        treeLeaves.removeAll(treeExecutors);
        return treeLeaves.contains(topAdditionalExecutor);
    }

    private boolean isBranch(StakeHolder topAdditionalExecutor, List<Branch> tree){
        Set<StakeHolder> treeExecutors = tree.stream().map(Branch::getExecutor).collect(Collectors.toSet());
        return treeExecutors.contains(topAdditionalExecutor);
    }

    /* Step to merge two tree when they do not have joint branch (the condition is already checked)
    * -> If is new tree attached to original tree:
    * -> build and connect MoneyNodeBranch from the toAdd group and not the tree group and vice versa
    * -> If it is new tree attached to original tree the targetRoot is the newly created top root
    * -> if not then the targetRoot is the root of the tree, also perform tree root swap
    * -> in the rootNode
    * */

    private Responsibility getLeafConnection(List<MoneyNodeBranch> newMoneyNodesBranches, ContractNode fetched,
                                             Map<StakeHolder, StakeHolderLeaf> leavesMap, Boolean addToOriginal){
        StakeHolderLeaf executorLeaf = leavesMap.get(fetched.getContract().getExecutor());
        if (addToOriginal) {
            return responsibilityService.findBySourceAndTarget(fetched.getId(), executorLeaf.getId())
                    .orElseThrow();
        }

        return newMoneyNodesBranches.stream()
                .filter(moneyNodeBranch -> moneyNodeBranch.getSwitchTarget().containsKey(executorLeaf))
                .map(moneyNodeBranch -> moneyNodeBranch.getSwitchTarget().get(executorLeaf))
                .findFirst().orElseThrow();
    }

    private IpBasedContract toFetch(List<Branch> toAddBranches, List<Branch> treeBranches, Boolean addToOriginal){
        if (!addToOriginal){
            return toAddBranches.get(0).getSortedContract().get(0);
        }
        Branch haveLeaf = treeBranches.stream().filter(branch -> branch.haveTarget(toAddBranches.get(0)))
                .findFirst().orElseThrow();
        return haveLeaf.getContracts().stream().filter(
                contract -> contract.getContractParticipants().stream().anyMatch(
                        participant -> participant.getStakeholder().equals(toAddBranches.get(0).getExecutor())
                )
        ).findFirst().orElseThrow();
    }

    private List<MoneyNodeBranch> getNewTreeNodesLeafCase(List<Branch> toAddBranches, List<Branch> treeBranches,
                                                Map<StakeHolder, StakeHolderLeaf> leavesMap, Boolean addToOriginal){
        List<Branch> newBranches = addToOriginal ? toAddBranches : treeBranches;
        List<MoneyNodeBranch> newBuilt = newBranches.stream().map(
                branch -> new Builder(branch).getLeaves(leavesMap)
                        .addNewContractNodeBuilder(contractNodeService::save).build()
        ).toList();
        connectNewBuiltBranch(newBuilt);
        return newBuilt;
    }

    private void mergeToLeaf(List<Branch> toAddBranches, List<Branch> treeBranches,
                             Map<StakeHolder, StakeHolderLeaf> leavesMap, Boolean addToOriginal){
        List<MoneyNodeBranch> newMoneyNodesBranches = getNewTreeNodesLeafCase(toAddBranches, treeBranches, leavesMap, addToOriginal);
        ContractNode fetched = contractNodeService.findByContractId(
                toFetch(toAddBranches, treeBranches, addToOriginal).getId()
        ).orElseThrow();
        Responsibility connection = getLeafConnection(newMoneyNodesBranches, fetched, leavesMap, addToOriginal);
        ContractNode connectionTarget = addToOriginal ? newMoneyNodesBranches.get(0).getBranchRoot() : fetched;
        connection.setTarget(connectionTarget);
        Set<Responsibility> allResponsibility = newMoneyNodesBranches.stream().map(MoneyNodeBranch::getResponsibilities)
                .map(HashSet::new).flatMap(Set::stream).collect(Collectors.toSet());
        allResponsibility.add(connection);
        responsibilityService.saveAll(allResponsibility);
    }

    private Map<Long,Collection<Responsibility>> upperNodeLoader(Collection<Long> nodeIds){
        Set<Responsibility> allResponsibility = responsibilityService.findUpstreamEdgesByNodeIds(nodeIds);
        return allResponsibility.stream().collect(
                Collectors.groupingBy(responsibility -> responsibility.getTarget().getId())
        ).entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, entry ->  new HashSet<>(entry.getValue())
        ));
    }

    private void mergeToBranch(List<Branch> toAddBranches, List<Branch> treeBranches,
                                             Map<StakeHolder, StakeHolderLeaf> leavesMap, Boolean addToOriginal){
        List<Branch> newBranchesList = addToOriginal ? toAddBranches : treeBranches;
        Set<Branch> newBranches = new HashSet<>(newBranchesList);
        System.out.println("New branches size is: " + newBranches.size());
        Branch toAddOverlap = toAddBranches.get(0);
        Branch treeOverlap = treeBranches.stream()
                .filter(branch -> branch.getExecutor().equals(toAddOverlap.getExecutor()))
                .findFirst().orElseThrow();
        Branch oldBranch = addToOriginal ? treeOverlap : toAddOverlap;
        Branch newBranch = addToOriginal ? toAddOverlap : treeOverlap;
        newBranches.remove(newBranch);
        MoneyNodeBranch mergeBranch = new Loader(oldBranch).addBranch(newBranch).getLeaves(leavesMap)
                .addNewContractNodeBuilder(contractNodeService::save)
                .addNewContractNodeLoader(contractNodeService::findByContracts)
                .addResponsibilityLoader(responsibilityService::findBySourceAndTarget)
                .addUpperNodeLoader(this::upperNodeLoader)
                .build();

        Set<MoneyNodeBranch> newBuilt = newBranches.stream().map(
                branch -> new Builder(branch).getLeaves(leavesMap)
                        .addNewContractNodeBuilder(contractNodeService::save).build()
        ).collect(Collectors.toSet());

        newBuilt.add(mergeBranch);

        List<MoneyNodeBranch> newBuiltBranchesList = newBuilt.stream().sorted(
                (node1, node2) -> Integer.compare(
                        node2.getBranch().getExecutor().getRole().getPriority(),
                        node1.getBranch().getExecutor().getRole().getPriority()
                )
        ).toList();

        connectNewBuiltBranch(newBuiltBranchesList);
        Set<Responsibility> allResponsibility = newBuiltBranchesList.stream().map(MoneyNodeBranch::getResponsibilities)
                .map(HashSet::new).flatMap(Set::stream).collect(Collectors.toSet());
        responsibilityService.saveAll(allResponsibility);
    }

    private void addNewContracts(Set<Branch> newBranchesGroup, List<Branch> currentBranches,
                                         Map<StakeHolder, StakeHolderLeaf> leavesMap){
        List<Branch> sortedNewBranches = newBranchesGroup.stream().sorted(
                Comparator.comparingInt(branch -> - branch.getExecutor().getRole().getPriority())
        ).toList();

        StakeHolder topNewBranchesExecutor = sortedNewBranches.get(0).getExecutor();
        StakeHolder topCurrentBranchesExecutor = currentBranches.get(0).getExecutor();

        if (isLeaf(topNewBranchesExecutor, sortedNewBranches)){
            System.out.println("Case add new tree to the current branch, with the top new being leaf");
            mergeToLeaf(currentBranches, sortedNewBranches, leavesMap, false);
            return;
        }
        if (isLeaf(topCurrentBranchesExecutor, currentBranches)){
            System.out.println("Case add current tree to the new branch, with the top current being leaf");
            mergeToLeaf(sortedNewBranches, currentBranches, leavesMap, true);
            return;
        }
        if (isBranch(topCurrentBranchesExecutor, sortedNewBranches)){
            System.out.println("Case add current tree to the new branch, with the top current being a branch of the target tree");
            mergeToBranch(currentBranches, sortedNewBranches, leavesMap, false);
            return;
        }
        System.out.println("Case add new tree to the current branch, with the top new being a branch of the target tree");
        mergeToBranch(sortedNewBranches, currentBranches, leavesMap, true);
    }

    private void addNewBranchGroups(List<Set<Branch>> connectedGroupsNewBranch, List<Branch> currentBranches,
                                    Map<StakeHolder, StakeHolderLeaf> leavesMap){
        connectedGroupsNewBranch.forEach(group -> addNewContracts(group, currentBranches, leavesMap));
    }

    public void insertContractsToExistedTree(List<IpBasedContract> newContracts, List<IpBasedContract> currentTreeContracts){
        List<Branch> newContractsBranches = getBranches(newContracts);
        List<Branch> currentBranches = getBranches(currentTreeContracts);
        List<Set<Branch>> connectedGroupsNewBranch = extractAllIsolatedBranchesGroup(newContractsBranches);
        Set<StakeHolder> allParticipant = new HashSet<>();
        newContractsBranches.forEach(branch -> {
            allParticipant.add(branch.getExecutor());
            allParticipant.addAll(branch.getNonExecutorSet());
        });
        Map<StakeHolder, StakeHolderLeaf> leavesMap = stakeHolderLeafService.getLeaves(allParticipant);
        addNewBranchGroups(connectedGroupsNewBranch, currentBranches, leavesMap);
    }
}
