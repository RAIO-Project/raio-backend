dependencies {
    api(project(":jwt-core"))
    api("org.springframework.boot:spring-boot-starter-security")
    compileOnly("org.springframework:spring-webmvc")
    compileOnly("jakarta.servlet:jakarta.servlet-api")
}
