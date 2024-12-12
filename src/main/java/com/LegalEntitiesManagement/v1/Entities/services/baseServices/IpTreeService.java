package com.LegalEntitiesManagement.v1.Entities.services.baseServices;
import com.LegalEntitiesManagement.v1.Common.Proxies.BaseService;
import com.LegalEntitiesManagement.v1.Entities.dto.IpTreeDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.IpTreeMapper;
import com.LegalEntitiesManagement.v1.Entities.exceptions.IpTreeNotFoundException;
import com.LegalEntitiesManagement.v1.Entities.model.GraphClass.IpTree;
import com.LegalEntitiesManagement.v1.Entities.repositories.IpTreeRepository;
import com.LegalEntitiesManagement.v1.Entities.services.UtilService;

import java.util.List;
import java.util.Optional;


public class IpTreeService implements BaseService<IpTree, IpTreeDto, Long>{
    private final IpTreeRepository ipTreeRepository;
    private final IpTreeMapper ipTreeMapper = IpTreeMapper.INSTANCE;

    public IpTreeService(IpTreeRepository ipTreeRepository) {
        this.ipTreeRepository = ipTreeRepository;
    }

    @Override
    public IpTree findById(Long id) {
        return ipTreeRepository.findById(id)
                .orElseThrow(() -> new IpTreeNotFoundException(id));
    }

    @Override
    public IpTree save(IpTree ipTree) {
        return ipTreeRepository.save(ipTree);
    }

    @Override
    public IpTree saveFromDto(IpTreeDto dto) {
        IpTree ipTree = ipTreeMapper.toEntity(dto);
        return save(ipTree);
    }

    @Override
    public boolean existsById(Long id) {
        return ipTreeRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        // Not implemented
    }

    @Override
    public List<IpTree> findAll() {
        return ipTreeRepository.findAll();
    }

    @Override
    public void verify(Object ipTreeInfo) {
        IpTree ipTree = UtilService.verifyAndGetEntity(
                ipTreeInfo,
                IpTree.class,
                IpTreeDto.class,
                ipTreeMapper,
                "The object must belong to either IpTree or IpTreeDto class"
        );
        if (!existsById(ipTree.getId())) {
            throw new IpTreeNotFoundException(ipTree.getId());
        }
    }

    // Custom repository method
    public Optional<IpTree> findByIntellectualPropertyId(Long ipId) {
        return ipTreeRepository.findByIntellectualPropertyId(ipId);
    }

    public boolean existsByIntellectualPropertyId(Long ipId) {
        return findByIntellectualPropertyId(ipId).isPresent();
    }
}
