dependencies {
    api("org.springframework.cloud:spring-cloud-starter-gateway")
    api("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    api("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // core
    api(project(":time-util"))
}

dependencyManagement {
    // About Spring Boot 3.4.x
    imports { mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.1") }
}