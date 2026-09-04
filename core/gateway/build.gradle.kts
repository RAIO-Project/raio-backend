dependencies {
    api("org.springframework.cloud:spring-cloud-starter-gateway")
    api("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    api("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // core
    api(project(":time-util"))
    // 서블릿 스택(jwt-webmvc)을 끌어오면 안 되므로 jwt-core 를 쓴다.
    api(project(":jwt-core"))
}

dependencyManagement {
    // About Spring Boot 3.4.x
    imports { mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.1") }
}