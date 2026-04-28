package raio.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "club")
@ConfigurationPropertiesScan(basePackages = "club")
public class BatchApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }
}