# grpc-server

초기 gRPC 서버 공통 모듈입니다.

## 역할

- 최소 예외 인터셉터 제공

## 설정 예시

```yaml
grpc:
  server:
    port: 9090
```

## 제공 Bean

- `ServerInterceptor grpcExceptionInterceptor`

도메인별 gRPC 어댑터에서 이 모듈을 의존하면 됩니다.
