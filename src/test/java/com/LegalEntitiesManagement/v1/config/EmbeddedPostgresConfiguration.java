package com.LegalEntitiesManagement.v1.config;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.SQLException;


@Configuration
@EnableJpaRepositories(basePackages = "com.LegalEntitiesManagement.v1.Entities.repositories")
@EntityScan(basePackages = "com.LegalEntitiesManagement.v1.Entities.model")
public class EmbeddedPostgresConfiguration {
    public static EmbeddedPostgres embeddedPostgres;
    private static DataSource dataSource;

    @Bean
    public DataSource dataSource() throws IOException, SQLException {
        if (dataSource == null){
            CreateDataSource();
        }

        return dataSource;
    }

    public static void CreateDataSource() throws IOException, SQLException {
        if (dataSource == null) {
            embeddedPostgres = EmbeddedPostgres.builder().start();
            dataSource = embeddedPostgres.getPostgresDatabase();
        }
    }

    private static void runMigrations(String path) throws SQLException {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(path)
                .load();
        flyway.migrate();
    }

    private static void runMigrations(String[] paths) throws SQLException {
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
    public static @interface ScriptPath {
        public String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface ScriptPaths {
        public String[] value();
        public String parentPath();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface ResetSequences {
        public String value();
    }

    public static class EmbeddedPostgresExtension implements AfterAllCallback, BeforeAllCallback {
        @Override
        public void afterAll(ExtensionContext context) throws Exception {
            System.out.println("Running the after all of the set up");
            if (embeddedPostgres == null) {
                return;
            }
            cleanDatabase();
            embeddedPostgres.close();
        }

        @Override
        public void beforeAll(ExtensionContext extensionContext) throws Exception {
            CreateDataSource();
            Class<?> testMethod = extensionContext.getRequiredTestClass();
            if (testMethod.isAnnotationPresent(ScriptPath.class)){
                ScriptPath scriptPath = testMethod.getAnnotation(ScriptPath.class);
                String path = scriptPath.value();
                runMigrations(path);
            }
            if (testMethod.isAnnotationPresent(ScriptPaths.class)){
                ScriptPaths scriptPath = testMethod.getAnnotation(ScriptPaths.class);
                String[] path = scriptPath.value();
                runMigrations(path);
            }
        }
    }
}
