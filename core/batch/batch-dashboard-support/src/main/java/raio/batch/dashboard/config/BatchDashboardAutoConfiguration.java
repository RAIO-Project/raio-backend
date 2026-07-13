package raio.batch.dashboard.config;

import org.springframework.batch.core.Job;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import raio.batch.dashboard.properties.BatchDashboardProperties;
import raio.batch.dashboard.support.SpringBatchMetadataSchemaInstaller;

import javax.sql.DataSource;

@AutoConfiguration
@ConditionalOnClass(Job.class)
@EnableConfigurationProperties(BatchDashboardProperties.class)
public class BatchDashboardAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public SpringBatchMetadataSchemaInstaller springBatchMetadataSchemaInstaller(
            DataSource dataSource,
            JdbcTemplate jdbcTemplate,
            BatchDashboardProperties properties
    ) {
        return new SpringBatchMetadataSchemaInstaller(dataSource, jdbcTemplate, properties);
    }
    
    @Bean
    public ApplicationRunner springBatchMetadataSchemaInitializer(
            SpringBatchMetadataSchemaInstaller installer
    ) {
        return args -> installer.installIfRequired();
    }
}