package raio.grpc.client.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import raio.grpc.client.properties.GrpcClientProperties;

@Configuration
@EnableConfigurationProperties(GrpcClientProperties.class)
public class GrpcClientConfig {
    
    private final GrpcClientProperties properties;
    
    public GrpcClientConfig(GrpcClientProperties properties) {
        this.properties = properties;
    }
    
    public ManagedChannel createChannel(String serviceName) {
        var client = properties.getRequiredClient(serviceName);
        
        ManagedChannelBuilder<?> builder =
                ManagedChannelBuilder.forTarget(client.target());
        
        if (client.isPlaintext()) {
            builder.usePlaintext();
        }
        
        return builder.build();
    }
}