package com.LegalEntitiesManagement.v1.unitTests.ServicesTests.GraphBuilderService;

import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.ContractNode;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.Responsibility;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import com.LegalEntitiesManagement.v1.Entities.repositories.ContractNodeRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.IpTreeRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.ResponsibilityRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.StakeHolderLeafRepository;
import com.LegalEntitiesManagement.v1.Entities.services.GraphBuilderService;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.*;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory;
import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import java.util.*;
import java.util.stream.Collectors;

import com.LegalEntitiesManagement.v1.unitTests.TestDataFactory.*;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public abstract class GraphBuilderServiceMockDependencies {

    @Mock
    IpTreeRepository ipTreeRepository;

    @Mock
    ContractNodeRepository contractNodeRepository;

    @Mock
    StakeHolderLeafRepository stakeHolderLeafRepository;

    @Mock
    ResponsibilityRepository responsibilityRepository;

    @Spy
    IpTreeService ipTreeService = new IpTreeService(ipTreeRepository);

    @Spy
    ContractNodeService contractNodeService = new ContractNodeService(contractNodeRepository);

    @Spy
    StakeHolderLeafService stakeHolderLeafService = new StakeHolderLeafService(stakeHolderLeafRepository);

    @Spy
    ResponsibilityService  responsibilityService = new ResponsibilityService(responsibilityRepository);

    protected Role participantRole;
    protected Counter stakeHolderCounter, ipBasedContractCounter, participantCounter, moneyNodeCounter, responsibilityCounter;
    protected StakeHolder topExecutor, secondExecutor, thirdExecutor;
    protected IntellectualProperty ip;
    protected GraphBuilderService graphBuilderService;

    protected ArrayList<IpBasedContract> originalContracts;

    protected Map<StakeHolder, StakeHolderLeaf> mapLeaves;

    protected Map<Long, ContractNode> mapContractNodeByContractId;
    protected Map<Long, Collection<Responsibility>> mapDownStreamEdges;
    protected Map<Long, Collection<Responsibility>> mapUpperStreamEdges;

    protected  ContractNode treeRoot;

    @BeforeEach
    void setUp() {
        Role topExecutorRole = new Role("Top Executor", "Mock top executor", 6);
        topExecutorRole.setId(1L);

        Role secondExecutorRole = new Role("Second Executor", "Mock Second executor", 5);
        secondExecutorRole.setId(2L);

        Role thirdExecutorRole = new Role("Third Executor", "Mock Third executor", 4);
        thirdExecutorRole.setId(3L);

        participantRole = new Role("Participant role", "Mock participant", 1);
        participantRole.setId(4L);

        ip = new IntellectualProperty();
        ip.setName("Test Ip");
        ip.setId(1L);

        stakeHolderCounter = new Counter();
        ipBasedContractCounter = new Counter();
        participantCounter = new Counter();
        moneyNodeCounter = new Counter();
        responsibilityCounter = new Counter();

        topExecutor = TestDataFactory.genStakeHolder(topExecutorRole, stakeHolderCounter);
        secondExecutor = TestDataFactory.genStakeHolder(secondExecutorRole, stakeHolderCounter);
        thirdExecutor = TestDataFactory.genStakeHolder(thirdExecutorRole, stakeHolderCounter);

        mapLeaves = TestDataFactory.genMapLeaves(moneyNodeCounter , topExecutor, secondExecutor, thirdExecutor);

        // Mock the services

        graphBuilderService = new GraphBuilderService(ipTreeService, contractNodeService, stakeHolderLeafService, responsibilityService);
        generateOriginalContracts();
    }


    protected void setNewTree(){
        treeContractNodeInfo newInfo = TestDataFactory.genTreeContractNodes(originalContracts, moneyNodeCounter, responsibilityCounter, mapLeaves);
        mapContractNodeByContractId = newInfo.mapContractNodeByContractId();
        mapDownStreamEdges = newInfo.mapDownStreamEdges();
        mapUpperStreamEdges = newInfo.mapUpperStreamEdges();
        treeRoot = newInfo.topNode();
    }

    protected void generateOriginalContracts(){
        TestDataFactory.MultiContractParticipants topExecutorContractsInfo = TestDataFactory.genBranchContractsParticipant(participantRole, stakeHolderCounter,
                2,3, topExecutor);
        List<List<StakeHolder>> topExecutorContractsParticipants = topExecutorContractsInfo.allContractsParticipant();
        Map<List<StakeHolder>, Integer> topBranchOriginalParticipantMap = new HashMap<>();
        topBranchOriginalParticipantMap.put(topExecutorContractsParticipants.get(0), 6);
        topBranchOriginalParticipantMap.put(topExecutorContractsParticipants.get(1), 5);

        originalContracts = new ArrayList<>(TestDataFactory.genBranchContractsWithPriorities(
                topExecutor,
                topBranchOriginalParticipantMap,
                ip,
                ipBasedContractCounter,
                participantCounter
        ));

        IpBasedContract connectToSecondExecutor = TestDataFactory.genIpBasedContract(topExecutor, Arrays.asList(topExecutor, secondExecutor),
                ip, ipBasedContractCounter, participantCounter, 4);
        originalContracts.add(connectToSecondExecutor);

        // gen second branchesOfOriginalTree
        TestDataFactory.MultiContractParticipants secondExecutorContractsInfo = TestDataFactory.genBranchContractsParticipant(participantRole, stakeHolderCounter,
                2,3, secondExecutor);
        List<List<StakeHolder>> secondExecutorContractsParticipants = secondExecutorContractsInfo.allContractsParticipant();

        Map<List<StakeHolder>, Integer> secondBranchOriginalParticipantMap = new HashMap<>();
        secondBranchOriginalParticipantMap.put(secondExecutorContractsParticipants.get(0), 6);
        secondBranchOriginalParticipantMap.put(secondExecutorContractsParticipants.get(1), 5);
        secondBranchOriginalParticipantMap.put(secondExecutorContractsParticipants.get(2), 4);

        originalContracts.addAll(TestDataFactory.genBranchContractsWithPriorities(
                secondExecutor,
                secondBranchOriginalParticipantMap,
                ip,
                ipBasedContractCounter,
                participantCounter
        ));

        Set<StakeHolder> allNonExecutorParticipants = originalContracts.stream()
                .map(Contract::getContractParticipants).flatMap(Set::stream).collect(Collectors.toSet())
                .stream().map(ContractParticipant::getStakeholder)
                .filter(stakeHolder -> !mapLeaves.containsKey(stakeHolder)).collect(Collectors.toSet());

        mapLeaves.putAll(TestDataFactory.genMapLeaves(moneyNodeCounter,
                allNonExecutorParticipants.toArray(StakeHolder[]::new))
        );
    }

    protected void setUpContractNodeSave() {
        doAnswer(invocation -> {
            ContractNode node = invocation.getArgument(0);
            return contractNodeServiceSave(node);
        }).when(contractNodeService).save(any(ContractNode.class));
    }

    protected ContractNode contractNodeServiceSave(ContractNode node){
        if (node.getId() == null) {
            node.setId(moneyNodeCounter.getId());
        }
        return node;
    }

    protected void setUpContractNodeFindByContractId() {
        doAnswer(invocation -> {
            Long contractId = invocation.getArgument(0);
            return Optional.ofNullable(mapContractNodeByContractId.get(contractId));
        }).when(contractNodeService).findByContractId(anyLong());
    }

    protected void setUpContractNodeFindByContracts() {
        doAnswer(invocation -> {
            Collection<? extends Contract> contracts = invocation.getArgument(0);
            return contracts.stream()
                    .map(contract -> mapContractNodeByContractId.get(contract.getId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }).when(contractNodeService).findByContracts(any());
    }

    protected void setUpDeleteContractNode(){
        doAnswer(invocationOnMock -> null).when(contractNodeService).deleteById(anyLong());
    }

    // StakeHolderLeaf Service Methods
    protected void setUpStakeHolderLeafFindById() {
        doAnswer(invocation -> {
            Long stakeholderId = invocation.getArgument(0);
            return mapLeaves.values().stream()
                    .filter(leaf -> leaf.getStakeHolder().getId().equals(stakeholderId))
                    .findFirst()
                    .orElse(null);
        }).when(stakeHolderLeafService).findByStakeholderId(anyLong());
    }

    protected void setUpStakeHolderLeafGetLeaves() {
        doAnswer(invocation -> {
            Collection<StakeHolder> stakeHolders = invocation.getArgument(0);
            return stakeHolders.stream()
                    .filter(mapLeaves::containsKey)
                    .collect(Collectors.toMap(
                            stakeHolder -> stakeHolder,
                            mapLeaves::get
                    ));
        }).when(stakeHolderLeafService).getLeaves(any());
    }

    // Responsibility Service Methods
    protected void setUpResponsibilityFindDownstreamEdges() {
        doAnswer(invocation -> {
            Long nodeId = invocation.getArgument(0);
            return new HashSet<>(
                    Optional.ofNullable(mapDownStreamEdges.get(nodeId))
                            .orElse(Collections.emptyList())
            );
        }).when(responsibilityService).findDownstreamEdges(anyLong());
    }

    protected void setUpResponsibilityFindUpstreamEdges() {
        doAnswer(invocation -> {
            Long nodeId = invocation.getArgument(0);
            return new HashSet<>(
                    Optional.ofNullable(mapUpperStreamEdges.get(nodeId))
                            .orElse(Collections.emptyList())
            );
        }).when(responsibilityService).findUpstreamEdges(anyLong());
    }

    protected void setUpResponsibilityFindBySourceAndTarget() {
        doAnswer(invocation -> {
            Long sourceId = invocation.getArgument(0);
            Long targetId = invocation.getArgument(1);
            Collection<Responsibility> sourceEdges = mapDownStreamEdges.get(sourceId);
            assert sourceEdges != null;

            if(sourceEdges.isEmpty()){
                return Optional.empty();
            }

            return sourceEdges.stream().filter(
                    responsibility -> responsibility.getTarget().getId().equals(targetId)
            ).findFirst();

        }).when(responsibilityService).findBySourceAndTarget(anyLong(), anyLong());
    }

    protected void setUpResponsibilitySave() {
        doAnswer(invocation -> {
            Responsibility responsibility = invocation.getArgument(0);
            if (responsibility.getId() == null) {
                responsibility.setId(responsibilityCounter.getId());
            }
            return responsibility;
        }).when(responsibilityService).save(any(Responsibility.class));
    }

    protected void setUpResponsibilitySaveAll() {
        doAnswer(invocation -> {
            Collection<Responsibility> responsibilities = invocation.getArgument(0);
            return responsibilities.stream()
                    .peek(r -> {
                        if (r.getId() == null) {
                            r.setId(responsibilityCounter.getId());
                        }
                    })
                    .collect(Collectors.toList());
        }).when(responsibilityService).saveAll(any());
    }

    protected void setUpResponsibilityFindUpstreamEdgesByNodeIds() {
        doAnswer(invocation -> {
            Collection<Long> nodeIds = invocation.getArgument(0);
            return nodeIds.stream()
                    .map(mapUpperStreamEdges::get)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        }).when(responsibilityService).findUpstreamEdgesByNodeIds(any());
    }

    protected void setUpResponsibilityServiceDeleteAll(){
        doAnswer(invocationOnMock -> null).when(responsibilityService).deleteAll(anyCollection());
    }

    // IpTree Service Methods
    protected void setUpIpTreeSave() {
        doAnswer(invocation -> invocation.getArgument(0))
                .when(ipTreeService).save(any());
    }

    // Operation-specific setup methods that combine individual mocks
    protected void setUpMockBehaviorsForBuildNewTree() {
        setUpContractNodeSave();
        setUpStakeHolderLeafGetLeaves();
        setUpResponsibilitySaveAll();
        setUpIpTreeSave();
    }

    protected void setUpMockBehaviorsForValidateDelete() {
        setUpContractNodeFindByContractId();
        setUpResponsibilityFindDownstreamEdges();
    }

    protected void setUpMockBehaviorsForDelete() {
        setUpContractNodeFindByContractId();
        setUpResponsibilityFindDownstreamEdges();
        setUpResponsibilityFindUpstreamEdges();
        setUpStakeHolderLeafFindById();
        setUpResponsibilitySave();
        setUpDeleteContractNode();
        setUpResponsibilityServiceDeleteAll();
    }

    protected void setUpMockBehaviorsForUpdate() {
        setUpContractNodeFindByContractId();
        setUpResponsibilityFindDownstreamEdges();
        setUpResponsibilityFindUpstreamEdges();
        setUpResponsibilityFindBySourceAndTarget();
        setUpResponsibilitySave();
        setUpResponsibilitySaveAll();
        setUpStakeHolderLeafFindById();
        setUpResponsibilityServiceDeleteAll();
        setUpStakeHolderLeafGetLeaves();
    }

    protected void setUpMockBehaviorsForInsertToExistedTree() {
        setUpContractNodeSave();
        setUpContractNodeFindByContracts();
        setUpStakeHolderLeafGetLeaves();
        setUpResponsibilityFindBySourceAndTarget();
        setUpResponsibilityFindUpstreamEdgesByNodeIds();
        setUpResponsibilitySaveAll();
    }
}
