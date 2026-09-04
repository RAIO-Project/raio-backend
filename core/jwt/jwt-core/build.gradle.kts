// 스택 중립 JWT 구현. 서블릿(jwt-webmvc)과 리액티브(gateway) 양쪽에서 재사용한다.
// spring-security / servlet 에 의존하지 않는 것이 이 모듈의 존재 이유이므로 추가하지 말 것.
dependencies {
    api(project(":jwt-api"))
    compileOnly("org.springframework.boot:spring-boot-autoconfigure")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
}
