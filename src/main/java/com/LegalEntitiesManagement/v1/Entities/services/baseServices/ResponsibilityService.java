package com.LegalEntitiesManagement.v1.Entities.services.baseServices;

import com.LegalEntitiesManagement.v1.Common.Proxies.BaseService;
import com.LegalEntitiesManagement.v1.Entities.dto.ResponsibilityDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.ResponsibilityMapper;
import com.LegalEntitiesManagement.v1.Entities.exceptions.ResponsibilityNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.Responsibility;
import com.LegalEntitiesManagement.v1.Entities.repositories.ResponsibilityRepository;
import com.LegalEntitiesManagement.v1.Entities.services.UtilService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ResponsibilityService implements BaseService<Responsibility, ResponsibilityDto, Long>{
    private final ResponsibilityRepository responsibilityRepository;
    private final ResponsibilityMapper responsibilitiesMapper = ResponsibilityMapper.INSTANCE;

    public ResponsibilityService(ResponsibilityRepository responsibilityRepository) {
        this.responsibilityRepository = responsibilityRepository;
    }

    @Override
    public Responsibility findById(Long id) {
        return responsibilityRepository.findById(id)
                .orElseThrow(() -> new ResponsibilityNotFoundException(id));
    }

    @Override
    public Responsibility save(Responsibility responsibility) {
        return responsibilityRepository.save(responsibility);
    }

    @Override
    public Responsibility saveFromDto(ResponsibilityDto dto) {
        Responsibility responsibility = responsibilitiesMapper.toEntity(dto);
        return save(responsibility);
    }

    public Optional<Responsibility> findBySourceAndTarget(Long sourceId, Long targetId) {
        return responsibilityRepository.findBySourceAndTarget(sourceId, targetId);
    }

    public Set<Responsibility> findDownstreamEdges(Long nodeId) {
        return responsibilityRepository.findDownstreamEdges(nodeId);
    }

    public Set<Responsibility> findUpstreamEdges(Long nodeId) {
        return responsibilityRepository.findUpstreamEdges(nodeId);
    }

    @Override
    public boolean existsById(Long id) {
        return responsibilityRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        this.responsibilityRepository.deleteById(id);
    }

    @Override
    public List<Responsibility> findAll() {
        return responsibilityRepository.findAll();
    }

    @Override
    public void verify(Object responsibilitiesInfo) {
        Responsibility responsibility = UtilService.verifyAndGetEntity(
                responsibilitiesInfo,
                Responsibility.class,
                ResponsibilityDto.class,
                responsibilitiesMapper,
                "The object must belong to either Responsibilities or ResponsibilitiesDto class"
        );
        if (!existsById(responsibility.getId())) {
            throw new ResponsibilityNotFoundException(responsibility.getId());
        }
    }

    public List<Responsibility> saveAll(Iterable<Responsibility> responsibilities){
        return this.responsibilityRepository.saveAll(responsibilities);
    }

    public void deleteAll(Iterable<Responsibility> responsibilities){
        this.responsibilityRepository.deleteAll(responsibilities);
    }

    public Set<Responsibility> findUpstreamEdgesByNodeIds (Collection<Long> ids){
        return this.responsibilityRepository.findUpstreamEdgesByNodeIds(ids);
    }
}
