import org.springframework.boot.gradle.tasks.bundling.BootJar

val payment: String by project

version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":batch-core"))

    implementation("org.springframework.boot:spring-boot-starter-batch")

    // dbProductFlywayConfig
    runtimeOnly("org.postgresql:postgresql:42.7.4")
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