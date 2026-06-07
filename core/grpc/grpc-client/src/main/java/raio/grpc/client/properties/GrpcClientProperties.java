package raio.grpc.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

@ConfigurationProperties(prefix = "app.grpc")
public record GrpcClientProperties(
        Map<String, Client> clients
) {
    
    public Client getRequiredClient(String name) {
        Client value = clients.get(name);
        
        if (value == null) {
            throw new IllegalArgumentException(
                    "gRPC client config not found: " + name
            );
        }
        
        return value;
    }
    
    public record Client(
            String target,
            Boolean plaintext,
            Duration timeout
    ) {
        
        public boolean isPlaintext() {
            return plaintext == null || plaintext;
        }
        
        public Duration getTimeout() {
            return timeout == null
                    ? Duration.ofSeconds(3)
                    : timeout;
        }
    }
}