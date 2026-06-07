# gRPC Architecture Guide

## Overview

본 프로젝트는 멀티모듈 구조에서 서비스 간 동기 통신을 위해 gRPC를 사용한다.

현재 구조는 다음 원칙을 따른다.

```txt
Application
  ↓
Port
  ↓
gRPC Client Adapter
  ↓
gRPC
  ↓
gRPC Server Adapter
  ↓
UseCase
```

Application 계층은 gRPC를 직접 알지 않는다.

gRPC는 Adapter 계층에만 존재하며, Application은 Port에만 의존한다.

---

# Why gRPC?

기존 REST 방식은 HTTP 요청/응답 기반으로 동작한다.

```txt
POST /api/resource
```

gRPC는 RPC(Remote Procedure Call) 방식으로 동작한다.

```txt
resourceService.create()
resourceService.update()
resourceService.delete()
```

즉, 네트워크 너머의 서비스를 마치 로컬 객체처럼 호출할 수 있다.

### 장점

```txt
- HTTP/2 기반
- 빠른 직렬화(Protobuf)
- 타입 안정성
- 코드 자동 생성
- 계약 기반(API First)
```

---

# Core Concepts

## Proto

gRPC의 API 계약서이다.

모든 gRPC API는 `.proto` 파일에서 정의한다.

```proto
service ResourceCommandService {
  rpc Create(CreateRequest)
      returns (CreateResponse);
}
```

proto 파일을 기준으로 다음 코드가 자동 생성된다.

```txt
Request
Response
Service Base Class
Client Stub
```

---

## Stub

Stub은 gRPC Client Proxy 객체이다.

클라이언트는 서버 구현체를 직접 호출하지 않는다.

대신 Stub을 사용한다.

```java
resourceStub.create(request);
```

내부적으로는 다음 흐름이 발생한다.

```txt
Stub
 ↓
HTTP/2 Request
 ↓
gRPC Server
 ↓
Response
```

Stub은 REST의 WebClient 또는 FeignClient와 비슷한 역할을 수행한다.

---

# Module Structure

## Server Side

```txt
service-a
 ├─ api
 │   └─ proto
 │
 ├─ application
 │
 └─ driving
     └─ grpc-server
```

### Responsibilities

#### api/proto

외부에 공개하는 gRPC 계약 정의

```txt
.proto
generated classes
```

#### application

비즈니스 로직

```txt
UseCase
Domain Service
```

#### driving/grpc-server

gRPC 요청 수신

```txt
Request → UseCase
UseCase → Response
```

---

## Client Side

```txt
service-b
 ├─ application
 │
 └─ driven
     └─ grpc-client
```

### Responsibilities

#### application

외부 서비스에 대한 Port 정의

```java
public interface ExternalCommandPort {
    void execute(String id);
}
```

#### driven/grpc-client

Port 구현

```java
@Component
public class GrpcExternalAdapter
        implements ExternalCommandPort {
}
```

Stub 호출은 Adapter 내부에서만 수행한다.

---

# Hexagonal Architecture

## Rule 1

Application은 gRPC를 알면 안 된다.

허용

```java
public interface ExternalCommandPort {
    void create(String id);
}
```

금지

```java
WalletCommandServiceBlockingStub
CreateRequest
CreateResponse
```

Application 계층은 Port만 의존한다.

---

## Rule 2

Proto 객체를 Domain으로 사용하지 않는다.

금지

```java
public Wallet create(CreateWalletRequest request)
```

허용

```java
public Wallet create(String userId)
```

Proto는 Adapter Layer에서 변환한다.

---

## Rule 3

gRPC는 Adapter Layer에만 존재한다.

```txt
application
 × gRPC

domain
 × gRPC

adapter
 ○ gRPC
```

---

# grpc-server-core

공통 gRPC 서버 기능을 제공한다.

```txt
grpc-server-core
```

주요 역할

```txt
- gRPC Starter 제공
- Server Interceptor 등록
- 공통 예외 처리
- Auto Configuration
```

예시

```java
@GrpcService
public class ResourceGrpcServerAdapter {
}
```

---

# grpc-client-core

공통 gRPC Client 기능을 제공한다.

```txt
grpc-client-core
```

주요 역할

```txt
- ManagedChannel 생성
- Client 설정 관리
- Timeout 설정
- TLS 설정
```

예시

```yaml
app:
  grpc:
    clients:
      resource:
        target: localhost:9090
        plaintext: true
        timeout: 3s
```

---

# Configuration

## Server

```yaml
grpc:
  server:
    port: 9090
```

---

## Client

```yaml
app:
  grpc:
    clients:
      resource:
        target: localhost:9090
        plaintext: true
        timeout: 3s
```

---

# Plaintext vs TLS

## Plaintext

```yaml
plaintext: true
```

```txt
암호화 없음
개발 환경
내부 네트워크
```

---

## TLS

```yaml
plaintext: false
```

```txt
TLS 암호화
인증서 검증
운영 환경
외부 네트워크
```

---

# Adding a New gRPC API

새로운 gRPC API 추가 시 아래 순서를 따른다.

## 1. Proto 정의

```proto
rpc Create(CreateRequest)
    returns (CreateResponse);
```

## 2. Proto Generate

generated source 생성

## 3. Server Adapter 구현

```java
@GrpcService
public class ResourceGrpcServerAdapter {
}
```

## 4. Application Port 정의

```java
public interface ResourceCommandPort {
}
```

## 5. Client Adapter 구현

```java
@Component
public class ResourceGrpcClientAdapter
        implements ResourceCommandPort {
}
```

## 6. Integration Test

실제 서비스 호출 검증

```txt
Client Adapter
 ↓
gRPC
 ↓
Server Adapter
 ↓
UseCase
```

---

# Future Improvements

현재 구조는 동기 RPC 호출에 초점을 맞춘다.

향후 확장 가능 영역

```txt
- Retry
- Circuit Breaker
- Distributed Tracing
- TLS
- Outbox Pattern
- Kafka Event Driven Architecture
- Service Discovery
```
