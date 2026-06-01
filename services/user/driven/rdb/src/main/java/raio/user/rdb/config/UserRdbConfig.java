package raio.user.rdb.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import raio.user.application.properties.AuthProperties;

@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class UserRdbConfig {
}
