dependencies {
    api(project(":batch-core"))

    implementation("org.springframework.boot:spring-boot-starter-batch")

    // dbProductFlywayConfig
    runtimeOnly("org.postgresql:postgresql:42.7.4")
}