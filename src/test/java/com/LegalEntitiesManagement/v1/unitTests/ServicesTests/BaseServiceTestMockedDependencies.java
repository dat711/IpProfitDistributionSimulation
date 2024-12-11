package com.LegalEntitiesManagement.v1.unitTests.ServicesTests;

import com.LegalEntitiesManagement.v1.Entities.repositories.IntellectualPropertyRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.RoleRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.StakeHolderRepository;
import com.LegalEntitiesManagement.v1.Entities.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTestMockedDependencies implements BeforeAllCallback {
    @Mock
    protected RoleRepository roleRepository;

    @Mock
    protected StakeHolderRepository stakeHolderRepository;

    @Mock
    protected IntellectualPropertyRepository intellectualPropertyRepository;

    @Mock
    protected ContractRepository contractRepository;

    @Mock
    protected IpBasedContractRepository ipBasedContractRepository;

    @Mock
    protected ContractParticipantRepository contractParticipantRepository;

    @Mock
    protected ContractNodeRepository contractNodeRepository;

    @Mock
    protected ResponsibilityRepository responsibilityRepository;

    @Mock
    protected StakeHolderLeafRepository stakeHolderLeafRepository;

    @Mock
    protected IpTreeRepository ipTreeRepository;

    protected void verifyMocksInitialized() {
        if (roleRepository == null) throw new IllegalStateException("RoleRepository mock not initialized");
        if (stakeHolderRepository == null) throw new IllegalStateException("StakeHolderRepository mock not initialized");
        if (intellectualPropertyRepository == null) throw new IllegalStateException("IntellectualPropertyRepository mock not initialized");
        if (contractRepository == null) throw new IllegalStateException("ContractRepository mock not initialized");
        if (ipBasedContractRepository == null) throw new IllegalStateException("IpBasedContractRepository mock not initialized");
        if (contractParticipantRepository == null) throw new IllegalStateException("ContractParticipantRepository mock not initialized");
        if (contractNodeRepository == null) throw new IllegalStateException("ContractNodeRepository mock not initialized");
        if (responsibilityRepository == null) throw new IllegalStateException("ResponsibilitiesRepository mock not initialized");
        if (stakeHolderLeafRepository == null) throw new IllegalStateException("StakeHolderLeafRepository mock not initialized");
        if (ipTreeRepository == null) throw new IllegalStateException("IpTreeRepository mock not initialized");
    }

    protected void setUpCommonMockBehaviors() {}

    @BeforeEach
    void baseSetUp() {
        setUpCommonMockBehaviors();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception{
        verifyMocksInitialized();
    }
}
