package raio.video.rdb.flyway;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "video.flyway")
public record VideoFlywayProperties(
        String locations,
        String schema,
        boolean createSchemas,
        boolean baselineOnMigrate,
        boolean validateOnMigrate,
        boolean outOfOrder,
        String historyTable
) {
}
