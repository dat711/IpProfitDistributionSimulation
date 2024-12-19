package com.LegalEntitiesManagement.v1.Entities.services.baseServices;
import com.LegalEntitiesManagement.v1.Entities.dto.IntellectualPropertyDto;
import com.LegalEntitiesManagement.v1.Entities.dto.mapper.IntellectualPropertyMapper;
import com.LegalEntitiesManagement.v1.Entities.exceptions.IpNotFoundException;
import com.LegalEntitiesManagement.v1.Common.Proxies.Updatable;
import com.LegalEntitiesManagement.v1.Entities.model.IntellectualProperty;
import com.LegalEntitiesManagement.v1.Entities.repositories.IntellectualPropertyRepository;
import com.LegalEntitiesManagement.v1.Entities.services.UtilService;
import com.LegalEntitiesManagement.v1.Common.Proxies.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IntellectualPropertyService implements BaseService<IntellectualProperty, IntellectualPropertyDto, Long>,
        Updatable<IntellectualProperty, IntellectualPropertyDto> {
    private final IntellectualPropertyRepository intellectualPropertyRepository;
    private final IntellectualPropertyMapper intellectualPropertyMapper = IntellectualPropertyMapper.INSTANCE;

    public IntellectualPropertyService(IntellectualPropertyRepository intellectualPropertyRepository) {
        this.intellectualPropertyRepository = intellectualPropertyRepository;
    }

    @Override
    public IntellectualProperty findById(Long id) {
        return this.intellectualPropertyRepository.findById(id)
                .orElseThrow(() -> new IpNotFoundException(id));
    }

    @Override
    public IntellectualProperty save(IntellectualProperty intellectualProperty) {
        IntellectualProperty saved = this.intellectualPropertyRepository.save(intellectualProperty);
        intellectualPropertyRepository.flush();
        return saved;
    }

    @Override
    public boolean existsById(Long id) {
        return this.intellectualPropertyRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        this.intellectualPropertyRepository.deleteById(id);
    }

    @Override
    public List<IntellectualProperty> findAll() {
        return this.intellectualPropertyRepository.findAll();
    }

    @Override
    public IntellectualProperty saveFromDto(IntellectualPropertyDto intellectualPropertyDto) {
        IntellectualProperty intellectualProperty = this.intellectualPropertyMapper.toEntity(intellectualPropertyDto);
        return this.save(intellectualProperty);
    }

    @Override
    public IntellectualProperty update(IntellectualProperty intellectualProperty) {
        return this.save(intellectualProperty);
    }

    @Override
    public IntellectualProperty updateFromDto(IntellectualPropertyDto intellectualPropertyDto) {
        IntellectualProperty intellectualProperty = this.intellectualPropertyMapper.toEntity(intellectualPropertyDto);
        return this.update(intellectualProperty);
    }

    @Override
    public void verify(Object ipInfo) {
        IntellectualProperty ip = UtilService.verifyAndGetEntity(
                ipInfo,
                IntellectualProperty.class,
                IntellectualPropertyDto.class,
                intellectualPropertyMapper,
                "The object must belong to either the IntellectualProperty or IntellectualPropertyDto class"
        );

        if (!this.existsById(ip.getId())) {
            throw new IpNotFoundException(ip.getId());
        }
    }
}
