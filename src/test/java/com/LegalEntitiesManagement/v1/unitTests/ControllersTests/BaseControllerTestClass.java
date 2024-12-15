package com.LegalEntitiesManagement.v1.unitTests.ControllersTests;

import com.LegalEntitiesManagement.v1.Common.aspects.logic.DtoConstraintsHandlerAspect;
import com.LegalEntitiesManagement.v1.Common.aspects.logic.ErrorsHandlerAspect;
import com.LegalEntitiesManagement.v1.Common.requestConstraints.validator.*;
import com.LegalEntitiesManagement.v1.V1Application;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = V1Application.class)
@Import({DtoConstraintsHandlerAspect.class,
        ErrorsHandlerAspect.class,
        ExistsValidator.class,
        DtoInsertChecking.class,
        TotalBenefitValidator.class,
        UpdateValidator.class
})
public class BaseControllerTestClass { }
