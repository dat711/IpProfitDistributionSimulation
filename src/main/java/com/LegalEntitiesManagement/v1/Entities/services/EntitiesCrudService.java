package com.LegalEntitiesManagement.v1.Entities.services;

import com.LegalEntitiesManagement.v1.Entities.dto.*;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ContractNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.IpNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.RoleNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.exceptions.StakeHolderNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.*;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.StakeHolderLeaf;
import com.LegalEntitiesManagement.v1.Entities.services.baseServices.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntitiesCrudService {
    private final IntellectualPropertyService intellectualPropertyService;
    private final StakeHolderService stakeHolderService;
    private final RoleService roleService;

    private final BaseContractParticipantService baseContractParticipantService;

    private final StakeHolderLeafService stakeHolderLeafService;

    private final BaseContractService contractService;

    private final GraphBuilderService graphBuilderService;

    private final IpBasedContractService ipBasedContractService;

    private final RoleMapper roleMapper = RoleMapper.INSTANCE;

    private final StakeHolderMapper stakeHolderMapper = StakeHolderMapper.INSTANCE;

    private final IntellectualPropertyMapper intellectualPropertyMapper = IntellectualPropertyMapper.INSTANCE;

    private final ParticipantMapper participantMapper = ParticipantMapper.INSTANCE;

    private final ContractMapper contractMapper = ContractMapper.INSTANCE;

    private final IpBasedContractMapper ipBasedContractMapper = IpBasedContractMapper.INSTANCE;

    public EntitiesCrudService(IntellectualPropertyService intellectualPropertyService, StakeHolderService stakeHolderService, RoleService roleService, BaseContractParticipantService baseContractParticipantService, StakeHolderLeafService stakeHolderLeafService, BaseContractService contractService, GraphBuilderService graphBuilderService, IpBasedContractService ipBasedContractService) {
        this.intellectualPropertyService = intellectualPropertyService;
        this.stakeHolderService = stakeHolderService;
        this.roleService = roleService;
        this.baseContractParticipantService = baseContractParticipantService;
        this.stakeHolderLeafService = stakeHolderLeafService;
        this.contractService = contractService;
        this.graphBuilderService = graphBuilderService;
        this.ipBasedContractService = ipBasedContractService;
    }

    public RoleDto addRole(RoleDto roleDto){
        Role savedRole = this.roleService.saveFromDto(roleDto);
        return roleMapper.toDto(savedRole);
    }

    public RoleDto getRole(Long id) {
        Role queriedRole = this.roleService.findById(id);
        return roleMapper.toDto(queriedRole);
    }

    public List<RoleDto> getAllRoles() {
        List<Role> roles = this.roleService.findAll();
        return roleMapper.toDtoList(roles);
    }

    public RoleDto updateRole(RoleDto roleDto){
        Role updatedRole = this.roleService.updateFromDto(roleDto);
        return roleMapper.toDto(updatedRole);
    }

    // IntellectualProperty CRUD operations
    public IntellectualPropertyDto addIntellectualProperty(IntellectualPropertyDto ipDto) {
        IntellectualProperty savedIp = this.intellectualPropertyService.saveFromDto(ipDto);
        return intellectualPropertyMapper.toDto(savedIp);
    }

    public IntellectualPropertyDto updateIntellectualProperty(IntellectualPropertyDto ipDto) {
        this.intellectualPropertyService.verify(ipDto);
        IntellectualProperty updatedIp = this.intellectualPropertyService.updateFromDto(ipDto);
        return intellectualPropertyMapper.toDto(updatedIp);
    }

    public IntellectualPropertyDto getIntellectualProperty(Long id) {
        IntellectualProperty ip = this.intellectualPropertyService.findById(id);
        return intellectualPropertyMapper.toDto(ip);
    }

    public List<IntellectualPropertyDto> getAllIntellectualProperties() {
        List<IntellectualProperty> ips = this.intellectualPropertyService.findAll();
        return intellectualPropertyMapper.toDtoList(ips);
    }

    // StakeHolder CRUD operations
    public StakeHolderDto addStakeHolder(StakeHolderDto stakeHolderDto) {
        // Verify that the role exists before creating the StakeHolder
        this.roleService.findById(stakeHolderDto.getRoleId());
        StakeHolder savedStakeHolder = this.stakeHolderService.saveFromDto(stakeHolderDto);
        StakeHolderLeaf leaf = new StakeHolderLeaf(savedStakeHolder);
        stakeHolderLeafService.save(leaf);
        return stakeHolderMapper.toDto(savedStakeHolder);
    }

    public StakeHolderDto updateStakeHolder(StakeHolderDto stakeHolderDto) {
        // Verify both the StakeHolder and its role exist before updating
        this.stakeHolderService.verify(stakeHolderDto);
        StakeHolder updatedStakeHolder = this.stakeHolderService.updateFromDto(stakeHolderDto);
        return stakeHolderMapper.toDto(updatedStakeHolder);
    }

    public StakeHolderDto getStakeHolder(Long id) {
        StakeHolder stakeHolder = this.stakeHolderService.findById(id);
        return stakeHolderMapper.toDto(stakeHolder);
    }

    public List<StakeHolderDto> getAllStakeHolders() {
        List<StakeHolder> stakeHolders = this.stakeHolderService.findAll();
        return stakeHolderMapper.toDtoList(stakeHolders);
    }

    public boolean roleExists(Long id) {
        return roleService.existsById(id);
    }

    public boolean stakeholderExists(Long id) {
        return stakeHolderService.existsById(id);
    }

    public boolean intellectualPropertyExists(Long id) {
        return intellectualPropertyService.existsById(id);
    }

    public void deleteStakeHolder(Long id){
        if (!this.stakeholderExists(id)){
            throw new StakeHolderNotFoundException(id);
        }

        if (!this.findParticipantsByStakeHolderId(id).isEmpty()){
            throw new IllegalArgumentException("Exist StakeHolders depend on the attempted delete Role");
        }
        this.stakeHolderService.deleteById(id);
    }

    public void deleteRole(Long id){
        if (!this.roleExists(id)){
            throw new RoleNotFoundException(id);
        }

        if (this.stakeHolderExistByRoleId(id)){
            throw new IllegalArgumentException("Exist StakeHolders depend on the attempted delete Role");
        }
        this.roleService.deleteById(id);
    }

    public void deleteIntellectualProperty(Long id){
        if(!this.intellectualPropertyService.existsById(id)){
            throw new IpNotFoundException(id);
        }

        if (this.ipBasedContractService.existByIpId(id)){
            throw new IllegalArgumentException("Exist contract associate with this Ip");
        }

        this.intellectualPropertyService.deleteById(id);
    }

    public boolean stakeHolderExistByRoleId(Long id){
        return this.stakeHolderService.existByRoleId(id);
    }

    public Set<ContractParticipant> findParticipantsByStakeHolderId(Long id){
        return this.baseContractParticipantService.findByStakeholderId(id);
    }

    /*To do:
    * - Create a method to map between ParticipantDto and Contract Participant, the contract can be unsaved in the post step
    * when inject to the graphBuilderService, the validation step do not need the contract id from the participant
    * - Create a method to showcase the graph model
    * - Create a method to transform the return contracts to the new composite dto
    * - Create a method to validate if the contract is fit to be converted to ipBasedContractDto
    * - Finish TotalBenefit Validator
    * - Add method for adding ipBasedContracts in batch.
    * - Add method for update and delete respectively with distinguish between ipBasedContractService and contractService
    * */

    public ContractCompositionDto saveContract(ContractCompositionDto contractCompositionDto){
        ContractDto contractDto = contractCompositionDto.getContractDto();
        Set<ParticipantDto> participantDtos = contractCompositionDto.getParticipants();
        Contract savedContract = contractService.saveFromDto(contractDto);
        List<ContractParticipant> participants = participantMapper.toEntitySet(participantDtos).stream().toList();
        participants.forEach(participant -> participant.setContract(savedContract));
        Set<ContractParticipant> savedParticipants = new HashSet<>(baseContractParticipantService.saveAll(participants));
        return new ContractCompositionDto(contractMapper.toDto(savedContract), participantMapper.toDtoSet(savedParticipants));
    }

    public ContractCompositionDto getContract(Long id){
        if (!this.contractService.existsById(id)){
            throw new ContractNotFoundException(id);
        }

        Contract contract = this.contractService.findById(id);
        Set<ContractParticipant> allParticipants = this.baseContractParticipantService.findParticipantsByContractId(id);
        contract.setContractParticipants(allParticipants);
        return convertToCompositeDto(contract);
    }

    private ContractCompositionDto convertToCompositeDto(Contract contract){
        return new ContractCompositionDto(
                contractMapper.toDto(contract),
                participantMapper.toDtoSet(contract.getContractParticipants())
        );
    }

    private List<ContractCompositionDto> convertToListCompositeDtos(List<Contract> newContracts){
        return newContracts.stream().map(
                contract -> new ContractCompositionDto(
                        contractMapper.toDto(contract),
                        participantMapper.toDtoSet(contract.getContractParticipants())
                )
        ).toList();
    }

    private IpBasedContract injectedParticipantPreSavedIpBasedContract(IpBasedContractDto ipBasedContractDto,
                                                                Set<ParticipantDto> participantDtos){
        IpBasedContract ipBasedContract = ipBasedContractMapper.toEntity(ipBasedContractDto);
        Set<ContractParticipant> preSaved = participantMapper.toEntitySet(participantDtos);
        preSaved.forEach(contractParticipant -> contractParticipant.setContract(ipBasedContract));
        ipBasedContract.setContractParticipants(preSaved);
        return ipBasedContract;
    }

    public IpBasedContractCompositionDto saveIpBasedContract(IpBasedContractCompositionDto ipBasedContractCompositionDto){
        IpBasedContractDto ipBasedContractDto = ipBasedContractCompositionDto.getContractDto();
        Set<ParticipantDto> participantDtos = ipBasedContractCompositionDto.getParticipants();
        Long ipId = ipBasedContractDto.getIpId();

        if (!this.intellectualPropertyService.existsById(ipId)){
            throw new IllegalArgumentException("The Ip defined in the contract is not existed");
        }

        IpBasedContract ipBasedContract = injectedParticipantPreSavedIpBasedContract(ipBasedContractDto, participantDtos);

        if(!this.graphBuilderService.ipTreeService.existsById(ipId)){
            this.graphBuilderService.validateBuildNewTree(Collections.singletonList(ipBasedContract));
            SavedIpBasedContractDetails savedContractDetails = getSavedIpBasedContractDetails(ipBasedContractDto, participantDtos);
            IpBasedContract savedContract = savedContractDetails.savedContract;
            Set<ContractParticipant> savedParticipants = savedContractDetails.participants;
            this.graphBuilderService.buildNewTree(Collections.singletonList(savedContract));
            return new IpBasedContractCompositionDto(ipBasedContractMapper.toDto(savedContract),
                    participantMapper.toDtoSet(savedParticipants));
        }

        List<IpBasedContract> currentContracts = this.ipBasedContractService.findContractsByIpId(ipId).stream().toList();
        this.graphBuilderService.validateAddNewContractToExistedTree(Collections.singletonList(ipBasedContract),currentContracts);
        SavedIpBasedContractDetails savedContractDetails = getSavedIpBasedContractDetails(ipBasedContractDto, participantDtos);
        IpBasedContract savedContract = savedContractDetails.savedContract;
        Set<ContractParticipant> savedParticipants = savedContractDetails.participants;
        this.graphBuilderService.insertContractsToExistedTree(Collections.singletonList(savedContract), currentContracts);
        return new IpBasedContractCompositionDto(ipBasedContractMapper.toDto(savedContract),
                participantMapper.toDtoSet(savedParticipants));
    }

    private record SavedIpBasedContractDetails(IpBasedContract savedContract, Set<ContractParticipant> participants ){}

    private SavedIpBasedContractDetails getSavedIpBasedContractDetails(IpBasedContractDto ipBasedContractDto, Set<ParticipantDto> participantDtos){
        IpBasedContract savedContract = ipBasedContractService.saveFromDto(ipBasedContractDto);
        List<ContractParticipant> participants = participantMapper.toEntitySet(participantDtos).stream().toList();
        participants.forEach(participant -> participant.setContract(savedContract));
        Set<ContractParticipant> savedParticipants = new HashSet<>(baseContractParticipantService.saveAll(participants));
        savedContract.setContractParticipants(savedParticipants);
        return new SavedIpBasedContractDetails(savedContract, savedParticipants);
    }

    public List<IpBasedContractCompositionDto> savedAllIpBasedContract(List<IpBasedContractCompositionDto> ipBasedContractCompositionDtoList){
        Long ipId = ipBasedContractCompositionDtoList.get(0).getContractDto().getIpId();

        if (!this.intellectualPropertyService.existsById(ipId)){
            throw new IllegalArgumentException("The Ip defined in the contract is not existed");
        }

        ProcessBulkIpBasedContracts processBulkIpBasedContracts = getPreSavedIpBasedContractDetails(ipBasedContractCompositionDtoList);

        if(!this.graphBuilderService.ipTreeService.existsById(ipId)){
            this.graphBuilderService.validateBuildNewTree(processBulkIpBasedContracts.preSavedIpBasedContracts);
            List<IpBasedContract> newContracts = saveAllValidIpBasedContract(processBulkIpBasedContracts);
            this.graphBuilderService.buildNewTree(newContracts.stream().toList());
            return convertToListIpBasedCompositeDtos(newContracts);
        }
        List<IpBasedContract> currentContracts = this.ipBasedContractService.findContractsByIpId(ipId).stream().toList();
        this.graphBuilderService.validateAddNewContractToExistedTree(processBulkIpBasedContracts.preSavedIpBasedContracts,
                currentContracts);
        List<IpBasedContract> newContracts = saveAllValidIpBasedContract(processBulkIpBasedContracts);
        this.graphBuilderService.insertContractsToExistedTree(newContracts, currentContracts);

        return convertToListIpBasedCompositeDtos(newContracts);
    }

    private List<IpBasedContractCompositionDto> convertToListIpBasedCompositeDtos(List<IpBasedContract> newContracts){
        return newContracts.stream().map(
                contract -> new IpBasedContractCompositionDto(
                        ipBasedContractMapper.toDto(contract),
                        participantMapper.toDtoSet(contract.getContractParticipants())
                )
        ).toList();
    }

    private List<IpBasedContract> saveAllValidIpBasedContract(ProcessBulkIpBasedContracts processBulkIpBasedContracts){
        List<IpBasedContract> savedContract = this.ipBasedContractService
                .saveAll(processBulkIpBasedContracts.preSavedIpBasedContracts);
        Map<Integer, Set<ContractParticipant>> mapParticipantsByContractPriority =
                processBulkIpBasedContracts.mapParticipantsByContractPriority;
        savedContract.forEach(contract -> {
            Set<ContractParticipant> participants = mapParticipantsByContractPriority.get(contract.getContractPriority());
            participants.forEach(participant -> participant.setContract(contract));
        });

        List<ContractParticipant> allParticipants = mapParticipantsByContractPriority.values()
                .stream().flatMap(Set::stream).collect(Collectors.toSet()).stream().toList();

        List<ContractParticipant> savedParticipants = this.baseContractParticipantService.saveAll(allParticipants);
        Map<IpBasedContract, Set<ContractParticipant>> mapParticipantByContract = savedParticipants.stream()
                .collect(Collectors.groupingBy(ContractParticipant::getContract)).entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> (IpBasedContract) entry.getKey(),
                        entry -> new HashSet<>(entry.getValue())
                ));
        Set<IpBasedContract> newContracts = mapParticipantByContract.keySet();
        newContracts.forEach(
                contract -> contract.setContractParticipants(mapParticipantByContract.get(contract))
        );

        return newContracts.stream().toList();
    }

    private record ProcessBulkIpBasedContracts(List<IpBasedContract> preSavedIpBasedContracts,
                                               Map<Integer, Set<ContractParticipant>> mapParticipantsByContractPriority){}
    private ProcessBulkIpBasedContracts getPreSavedIpBasedContractDetails(List<IpBasedContractCompositionDto> ipBasedContractCompositionDtoList){
        Map<IpBasedContract, Set<ParticipantDto>> mapParticipantsByContractPreSavedContract = ipBasedContractCompositionDtoList.stream()
                .collect(Collectors.toMap(
                        ipBasedContractCompositionDto -> injectedParticipantPreSavedIpBasedContract(
                                ipBasedContractCompositionDto.getContractDto(), ipBasedContractCompositionDto.getParticipants()
                        ),
                        IpBasedContractCompositionDto::getParticipants
                ));

        Map<Integer, Set<ContractParticipant>> mapParticipantsByContractPriority = mapParticipantsByContractPreSavedContract.entrySet()
                .stream().collect(Collectors.toMap(
                        entry -> entry.getKey().getContractPriority(),
                        entry -> participantMapper.toEntitySet(entry.getValue())
                ));

        List<IpBasedContract> preSavedIpBasedContracts = mapParticipantsByContractPreSavedContract.keySet().stream().toList();
        return new ProcessBulkIpBasedContracts(preSavedIpBasedContracts, mapParticipantsByContractPriority);
    }
}
