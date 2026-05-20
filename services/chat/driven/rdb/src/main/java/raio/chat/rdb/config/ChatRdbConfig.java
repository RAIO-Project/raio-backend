package raio.chat.rdb.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.snowflake.persistence.id.Snowflake;
import raio.snowflake.properties.SnowflakeProperties;

@Configuration
@EnableConfigurationProperties(SnowflakeProperties.class)
public class ChatRdbConfig {

    @Bean
    public Snowflake snowflake(SnowflakeProperties properties) {
        return new Snowflake(properties);
    }
}