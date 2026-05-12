# User 서비스 아키텍처

Spring Security + JWT 기반 인증/인가 설계 문서.

---

## 전체 구조

```
raio-backend/
├── core/
│   └── jwt/
│       ├── jwt-api/        # JWT 인터페이스 (의존성 없음, 다른 서비스에서 재사용)
│       └── jwt-webmvc/     # Spring Security 필터 + 설정 구현체
│
└── services/user/
    ├── api/
    │   ├── domain/         # User 엔티티, Repository 포트 인터페이스
    │   ├── exception/      # 도메인 예외
    │   └── readmodel/      # 조회용 DTO
    ├── application/        # 유스케이스 (인터페이스 + 구현)
    ├── driven/rdb/         # DB 어댑터 (JPA 구현체)
    └── driving/web-mvc/    # HTTP 어댑터 (Controller + SecurityConfig)
```

---

## Core: JWT 모듈

기존 `cors-api` / `cors-webmvc` 패턴과 동일하게 분리.
다른 서비스(chat, stream 등)가 `jwt-webmvc`에만 의존하면 JWT 인증이 자동 적용됨.

### jwt-api

인터페이스만 정의. 라이브러리 의존성 없음.

```kotlin
interface JwtProvider {
    fun generate(userId: Long, roles: Set<String>): TokenPair
    fun validate(token: String): Boolean
    fun extractUserId(token: String): Long
    fun extractRoles(token: String): Set<String>
}

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)
```

### jwt-webmvc

`jwt-api` 구현체 + Spring Security 설정.

| 클래스 | 역할 |
|--------|------|
| `JwtProviderImpl` | JJWT 라이브러리로 `JwtProvider` 구현 |
| `JwtAuthenticationFilter` | 요청마다 Access Token 검증 후 `SecurityContext` 주입 |
| `JwtSecurityConfig` | `SecurityFilterChain` 기본 설정 (stateless, CSRF off) |

**build.gradle.kts 의존성:**
```kotlin
// jwt-api
dependencies {
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
}

// jwt-webmvc
dependencies {
    api(project(":jwt-api"))
    api("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
}
```

---

## User 서비스 레이어

### domain

도메인 엔티티와 포트(Repository 인터페이스)를 정의.
JPA 어노테이션 없음 — 순수 도메인 모델.

```kotlin
// User.kt
class User(
    val id: Long,
    val email: String,
    val passwordHash: String,
    val role: Role,
    val createdAt: LocalDateTime,
)

enum class Role { USER, ADMIN }

// UserRepository.kt (포트 인터페이스)
interface UserRepository {
    fun save(user: User): User
    fun findByEmail(email: String): User?
    fun findById(id: Long): User?
    fun existsByEmail(email: String): Boolean
}

// RefreshTokenRepository.kt (포트 인터페이스)
interface RefreshTokenRepository {
    fun save(userId: Long, token: String, ttl: Duration)
    fun findByUserId(userId: Long): String?
    fun delete(userId: Long)
}
```

### exception

```kotlin
class UserNotFoundException : DomainException("사용자를 찾을 수 없습니다")
class DuplicateEmailException : DomainException("이미 사용 중인 이메일입니다")
class InvalidCredentialsException : DomainException("이메일 또는 비밀번호가 올바르지 않습니다")
class InvalidTokenException : DomainException("유효하지 않은 토큰입니다")
```

### readmodel

조회 전용 DTO. 도메인 엔티티를 직접 반환하지 않음.

```kotlin
data class UserProfileReadModel(
    val id: Long,
    val email: String,
    val role: Role,
    val createdAt: LocalDateTime,
)
```

### application

유스케이스 인터페이스와 구현체를 함께 위치.
`JwtProvider`(jwt-api)를 주입받아 토큰 생성/검증.

```kotlin
// 인터페이스
interface RegisterUseCase { fun register(command: RegisterCommand): Long }
interface LoginUseCase    { fun login(command: LoginCommand): TokenPair }
interface LogoutUseCase   { fun logout(userId: Long) }
interface RefreshUseCase  { fun refresh(refreshToken: String): TokenPair }

// Command
data class RegisterCommand(val email: String, val password: String)
data class LoginCommand(val email: String, val password: String)

// 구현 예시 (LoginService)
@Service
class LoginService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProvider: JwtProvider,
    private val passwordEncoder: PasswordEncoder,
) : LoginUseCase {
    override fun login(command: LoginCommand): TokenPair {
        val user = userRepository.findByEmail(command.email)
            ?: throw InvalidCredentialsException()
        if (!passwordEncoder.matches(command.password, user.passwordHash))
            throw InvalidCredentialsException()

        val tokenPair = jwtProvider.generate(user.id, setOf(user.role.name))
        refreshTokenRepository.save(user.id, tokenPair.refreshToken, Duration.ofDays(14))
        return tokenPair
    }
}
```

**build.gradle.kts 의존성:**
```kotlin
dependencies {
    api(project(userApi))      // domain, exception 포함
    api(project(":jwt-api"))   // JwtProvider 인터페이스
    api("org.springframework.security:spring-security-crypto") // PasswordEncoder
}
```

### driven/rdb

포트 인터페이스의 JPA 구현체.

```kotlin
// UserJpaEntity.kt — JPA 어노테이션 포함
@Entity @Table(name = "tb_user")
class UserJpaEntity(
    @Id val id: Long,
    @Column(unique = true) val email: String,
    val passwordHash: String,
    @Enumerated(EnumType.STRING) val role: Role,
    val createdAt: LocalDateTime,
)

// UserRepositoryAdapter.kt — UserRepository 포트 구현
@Repository
class UserRepositoryAdapter(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun findByEmail(email: String) =
        userJpaRepository.findByEmail(email)?.toDomain()
    // ...
}
```

RefreshToken 저장소는 **Redis** 사용 권장 (TTL 관리 용이).
Redis 미사용 시 `tb_refresh_token` 테이블로 RDB에 저장.

**build.gradle.kts 의존성:**
```kotlin
dependencies {
    api(project(userApi))
    api(project(userApplication))
    api(project(":jpa-core"))
    api(project(":snowflake-id-hibernate"))
    // Redis 사용 시
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}
```

### driving/web-mvc

HTTP 요청을 받아 유스케이스를 호출. SecurityFilterChain 설정도 여기서 담당.

**엔드포인트:**

| Method | Path | 설명 | 인증 필요 |
|--------|------|------|-----------|
| POST | `/auth/register` | 회원가입 | X |
| POST | `/auth/login` | 로그인 → TokenPair 반환 | X |
| POST | `/auth/refresh` | Access Token 재발급 | X (Refresh Token) |
| POST | `/auth/logout` | 로그아웃 (Refresh Token 삭제) | O |
| GET | `/users/me` | 내 프로필 조회 | O |

```kotlin
// AuthController.kt
@RestController
@RequestMapping("/auth")
class AuthController(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val refreshUseCase: RefreshUseCase,
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): TokenPairResponse =
        loginUseCase.login(request.toCommand()).toResponse()
}

// UserSecurityConfig.kt — 이 서비스의 SecurityFilterChain
@Configuration
class UserSecurityConfig {
    @Bean
    fun userSecurityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .securityMatcher("/auth/**", "/users/**")
            .authorizeHttpRequests {
                it.requestMatchers("/auth/register", "/auth/login", "/auth/refresh").permitAll()
                it.anyRequest().authenticated()
            }
            .build()
}
```

**build.gradle.kts 의존성:**
```kotlin
dependencies {
    api(project(userApi))
    api(project(userApplication))
    api(project(":jwt-webmvc"))  // Spring Security 필터 + SecurityConfig 포함
}
```

---

## 다른 서비스에서 JWT 인증 재사용

다른 서비스의 `web-mvc/build.gradle.kts`에 한 줄 추가:

```kotlin
dependencies {
    api(project(":jwt-webmvc"))  // 이것만 추가하면 JWT 필터 자동 적용
    api(project(chatApi))
    api(project(chatApplication))
}
```

컨트롤러에서 현재 인증된 사용자 꺼내기:

```kotlin
@GetMapping("/rooms")
fun getRooms(authentication: Authentication): List<RoomResponse> {
    val userId = authentication.principal as Long
    return getChatRoomsUseCase.getByUser(userId)
}
```

---

## 모듈 의존성 전체 그림

```
[jwt-api]
    ↑
[jwt-webmvc]  ←── (각 서비스 web-mvc 모듈)

[user-domain] ←── [user-exception]
    ↑
[user-application] ──→ [jwt-api]
    ↑
[user-rdb]    [user-webmvc] ──→ [jwt-webmvc]
```

---

## 구현 순서

1. `core/jwt/jwt-api` 모듈 생성 + `core.settings.gradle.kts` 등록
2. `core/jwt/jwt-webmvc` 모듈 생성 (JJWT + Spring Security)
3. `user/api/domain` — User 엔티티, Repository 포트
4. `user/api/exception` — 도메인 예외
5. `user/api/readmodel` — 조회용 DTO
6. `user/application` — 유스케이스 구현 (JwtProvider 주입)
7. `user/driven/rdb` — JPA 어댑터 + Flyway 마이그레이션
8. `user/driving/web-mvc` — AuthController + SecurityConfig
9. `monolith/main-runner` — user 서비스 의존성 추가
