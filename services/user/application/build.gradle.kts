val userApi: String by project

dependencies {
    api(project(userApi))
    api(project(":jwt-api"))
    implementation("org.springframework.security:spring-security-crypto")
}
