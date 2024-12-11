package com.LegalEntitiesManagement.v1.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.annotation.*;
import java.sql.SQLException;
import java.util.Properties;


@Configuration
@EnableJpaRepositories(basePackages = "com.LegalEntitiesManagement.v1.Entities.repositories")
@EntityScan(basePackages = "com.LegalEntitiesManagement.v1.Entities.model")
public class TestDbServerConfiguration {
    private static DataSource dataSource;
    @Bean
    public DataSource dataSource() throws IOException, SQLException {
        if (dataSource == null){
            CreateDataSource();
        }

        return dataSource;
    }

    public static void CreateDataSource() throws IOException{
        if (dataSource == null) {
            dataSource = getDataSource();
        }
    }

    private static DataSource getDataSource() throws IOException {
        Properties testProps = getTestProperties();
        DriverManagerDataSource NewDataSource = new DriverManagerDataSource();
        NewDataSource.setDriverClassName(testProps.getProperty("spring.datasource.driver-class-name"));
        NewDataSource.setUrl(testProps.getProperty("test.database.url"));
        NewDataSource.setUsername(testProps.getProperty("test.database.username"));
        NewDataSource.setPassword(testProps.getProperty("test.database.password"));
        return NewDataSource;
    }

    private static Properties getTestProperties() throws IOException {
        return PropertiesLoaderUtils.loadProperties(
                new ClassPathResource("application-test.properties")
        );
    }

    private static void runMigrations(String path) throws SQLException {
        System.out.println("---------------------------------------");
        System.out.println("Running the migrations with single path of the set up");
        System.out.println("---------------------------------------");
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(path)
                .load();
        flyway.migrate();
    }

    private static void runMigrations(String[] paths) throws SQLException {
        System.out.println("---------------------------------------");
        System.out.println("Running the migrations with multiple paths of the set up");
        System.out.println("---------------------------------------");
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(paths)
                .load();
        flyway.migrate();
    }

    private static void showFlyWayConnection(Flyway flyway) throws SQLException {
        DataSource ds = flyway.getConfiguration().getDataSource();
        showDataSourceStates(ds);
    }

    private static void showDataSourceStates(DataSource ds){
        if (ds == null){
            System.out.println(	"The connection is null is: True" );
            return;
        }
        System.out.println(	"The connection is null is: False" );
    }

    private static void cleanDatabase() throws SQLException {
        Flyway flyway = Flyway.configure()
                .cleanDisabled(false)
                .dataSource(dataSource)
                .load();
        flyway.clean();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public static @interface ScriptPath {
        public String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public static @interface ScriptPaths {
        public String[] value();
        public String parentPath();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public static @interface ResetSequences {
        public String value();
    }

    public static class EmbeddedPostgresExtension implements AfterAllCallback, BeforeAllCallback {
        @Override
        public void afterAll(ExtensionContext context) throws Exception {
            System.out.println("---------------------------------------");
            System.out.println("Running the after all of the set up");
            System.out.println("---------------------------------------");
            if (dataSource == null) {
                return;
            }
            cleanDatabase();
        }

        private Class<?> traverseClassHierachy(Class<?> testClass) {
            System.out.println("---------------------------------------");
            System.out.println("The current class is " + testClass.toString() );
            System.out.println("---------------------------------------");
            if(testClass.isMemberClass()){
                System.out.println("---------------------------------------");
                System.out.println("The current class is a member class");
                System.out.println("---------------------------------------");
                return traverseClassHierachy(testClass.getDeclaringClass());
            }
            return testClass;
        }
        @Override
        public void beforeAll(ExtensionContext extensionContext) throws Exception {
            CreateDataSource();
        }
    }

}
