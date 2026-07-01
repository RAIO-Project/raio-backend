package raio.batch.dashboard.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.batch.dashboard")
public record BatchDashboardProperties(
        boolean enabled,
        Metadata metadata
) {
    
    public BatchDashboardProperties {
        metadata = metadata == null ? new Metadata(true, true) : metadata;
    }
    
    public record Metadata(
            boolean initializeSchema,
            boolean failOnError
    ) {
    }
}