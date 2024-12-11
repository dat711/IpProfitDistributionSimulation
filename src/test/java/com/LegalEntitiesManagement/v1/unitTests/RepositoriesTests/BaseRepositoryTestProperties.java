package com.LegalEntitiesManagement.v1.unitTests.RepositoriesTests;
import com.LegalEntitiesManagement.v1.config.TestDbServerConfiguration;
import com.LegalEntitiesManagement.v1.config.TestDbServerConfiguration.EmbeddedPostgresExtension;
import com.LegalEntitiesManagement.v1.config.TestDbServerConfiguration.ScriptPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ExtendWith(EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = { TestDbServerConfiguration.class })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class BaseRepositoryTestProperties {}
