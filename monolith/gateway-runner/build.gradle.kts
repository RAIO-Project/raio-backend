import org.springframework.boot.gradle.tasks.bundling.BootJar

version = "0.0.1-SNAPSHOT"

// dependency-management 플러그인은 "해석하는 프로젝트"의 BOM만 본다.
// :gateway가 버전 없이 선언한 spring-cloud 의존성을 이 모듈이 해석하므로 여기에도 BOM이 필요하다.
dependencyManagement {
    imports { mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.1") }
}

dependencies {
    // core
    api(project(":gateway"))
    // api(project(":monitoring-core"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootJar>{
    enabled = true
}

tasks.withType<Jar>{
    enabled = true
}