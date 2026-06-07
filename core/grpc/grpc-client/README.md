# grpc-client

초기 gRPC 클라이언트 공통 모듈입니다.

## 역할

- `app.grpc.client.*` 설정 바인딩
- `ManagedChannel` 생성 보조

## 설정 예시

```yaml
app:
  grpc:
    client:
      payment:
        target: localhost:9090
        plaintext: true
        timeout: 3s
```

## 사용 예시

```java
@Configuration
public class PaymentGrpcClientConfig {

    @Bean
    ManagedChannel paymentChannel(GrpcClientConfig config) {
        return config.createChannel("payment");
    }

    @Bean
    PaymentGrpcServiceGrpc.PaymentGrpcServiceBlockingStub paymentStub(ManagedChannel paymentChannel) {
        return PaymentGrpcServiceGrpc.newBlockingStub(paymentChannel);
    }
}
```
