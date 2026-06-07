package raio.grpc.server.config;

import io.grpc.ServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.grpc.server.interceptor.GrpcExceptionInterceptor;

@Configuration
public class GrpcServerConfig {

    @Bean
    public ServerInterceptor grpcExceptionInterceptor() {
        return new GrpcExceptionInterceptor();
    }
}
