package raio.stream.rdb.flyway;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stream.flyway")
public record StreamFlywayProperties(
        String locations,
        String schema,
        boolean createSchemas,
        boolean baselineOnMigrate,
        boolean validateOnMigrate,
        boolean outOfOrder,
        String historyTable
) {
}