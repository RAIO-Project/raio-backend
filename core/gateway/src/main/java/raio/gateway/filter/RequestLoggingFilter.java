package raio.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import raio.time.MillisecondsSupplier;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {
    
    private final MillisecondsSupplier millisecondsSupplier;
    
    public RequestLoggingFilter(MillisecondsSupplier millisecondsSupplier) {
        this.millisecondsSupplier = millisecondsSupplier;
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long requestTime = millisecondsSupplier.getAsLong();
        
        return chain.filter(exchange)
                // 요청 후 최종 응답 때
                .doFinally(signal -> {
                    // LB가 선택한 서비스 주소
                    URI target = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
                    
                    log.info("[GATEWAY] {} {} → target={}, status={}, {}ms",
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getURI().getPath(),
                            target,
                            exchange.getResponse().getStatusCode(),
                            System.currentTimeMillis() - requestTime);
        });
    }
    
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
