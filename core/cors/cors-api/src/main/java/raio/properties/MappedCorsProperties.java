package raio.properties;

import raio.properties.allowed.CorsAllowedProperties;
import raio.properties.exposed.CorsExposedProperties;

public record MappedCorsProperties(
        String path,
        CorsAllowedProperties allowed,
        CorsExposedProperties exposed,
        Long maxAge
) {
    public MappedCorsProperties {
        if (maxAge == null) {
            maxAge = 3600L;
        }
    }
}
