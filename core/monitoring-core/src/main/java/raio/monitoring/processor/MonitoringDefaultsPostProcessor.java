package raio.monitoring.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

public class MonitoringDefaultsPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> defaults = new LinkedHashMap<>();
        defaults.put("management.endpoints.web.exposure.include", "health,prometheus");
        defaults.put("management.endpoint.prometheus.enabled", "true");

        environment.getPropertySources().addLast(
                new MapPropertySource("monitoringDefaults", defaults)
        );
    }
}
