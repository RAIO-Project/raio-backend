package raio.batch.dashboard.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import raio.batch.dashboard.properties.BatchDashboardProperties;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Slf4j
@RequiredArgsConstructor
public class SpringBatchMetadataSchemaInstaller {
    
    private static final String CHECK_TABLE_SQL =
            "SELECT 1 FROM BATCH_JOB_INSTANCE WHERE 1 = 0";
    
    private static final String SCHEMA_PREFIX =
            "org/springframework/batch/core/schema-";
    
    private static final String SCHEMA_SUFFIX =
            ".sql";
    
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final BatchDashboardProperties properties;
    
    public void installIfRequired() {
        if (!properties.enabled()) {
            log.info("[BATCH-DASHBOARD] disabled");
            return;
        }
        
        if (!properties.metadata().initializeSchema()) {
            log.info("[BATCH-DASHBOARD] metadata schema initialization disabled");
            return;
        }
        
        if (batchMetadataExists()) {
            log.info("[BATCH-DASHBOARD] Spring Batch metadata schema already exists");
            return;
        }
        
        install();
    }
    
    private boolean batchMetadataExists() {
        try {
            jdbcTemplate.execute(CHECK_TABLE_SQL);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void install() {
        try (Connection connection = dataSource.getConnection()) {
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            String schemaLocation = resolveSchemaLocation(databaseProductName);
            
            log.info(
                    "[BATCH-DASHBOARD] installing Spring Batch metadata schema. database={}, schema={}",
                    databaseProductName,
                    schemaLocation
            );
            
            var resource = new EncodedResource(
                    new ClassPathResource(schemaLocation),
                    StandardCharsets.UTF_8
            );
            
            ScriptUtils.executeSqlScript(connection, resource);
            
            log.info("[BATCH-DASHBOARD] Spring Batch metadata schema installed");
            
        } catch (Exception e) {
            if (properties.metadata().failOnError()) {
                throw new IllegalStateException("Failed to install Spring Batch metadata schema", e);
            }
            
            log.warn("[BATCH-DASHBOARD] failed to install Spring Batch metadata schema", e);
        }
    }
    
    private String resolveSchemaLocation(String databaseProductName) {
        String db = databaseProductName.toLowerCase();
        
        if (db.contains("postgresql")) {
            return SCHEMA_PREFIX + "postgresql" + SCHEMA_SUFFIX;
        }
        
        if (db.contains("mysql")) {
            return SCHEMA_PREFIX + "mysql" + SCHEMA_SUFFIX;
        }
        
        if (db.contains("mariadb")) {
            return SCHEMA_PREFIX + "mariadb" + SCHEMA_SUFFIX;
        }
        
        if (db.contains("h2")) {
            return SCHEMA_PREFIX + "h2" + SCHEMA_SUFFIX;
        }
        
        if (db.contains("oracle")) {
            return SCHEMA_PREFIX + "oracle" + SCHEMA_SUFFIX;
        }
        
        if (db.contains("microsoft sql server")) {
            return SCHEMA_PREFIX + "sqlserver" + SCHEMA_SUFFIX;
        }
        
        throw new IllegalStateException(
                "Unsupported database for Spring Batch metadata schema: " + databaseProductName
        );
    }
}