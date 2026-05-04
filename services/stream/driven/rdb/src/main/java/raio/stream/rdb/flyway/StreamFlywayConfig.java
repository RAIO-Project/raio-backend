package raio.stream.rdb.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(StreamFlywayProperties.class)
public class StreamFlywayConfig {
    
    @Bean(initMethod = "migrate")
    public Flyway streamFlyway(DataSource dataSource, StreamFlywayProperties props) {
        
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(props.locations())
                .schemas(props.schema())
                .defaultSchema(props.schema())
                .createSchemas(props.createSchemas())
                .baselineOnMigrate(props.baselineOnMigrate())
                .validateOnMigrate(props.validateOnMigrate())
                .outOfOrder(props.outOfOrder())
                .table(props.historyTable())
                .load();
    }
}
