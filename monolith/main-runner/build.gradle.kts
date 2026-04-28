import org.springframework.boot.gradle.tasks.bundling.BootJar

val chat: String by project
val donation: String by project
val payment: String by project
val stream: String by project
val user: String by project

version = "0.0.1-SNAPSHOT"

dependencies {
    // service

    // core
    implementation(project(":exception-handler-core"))
    implementation(project(":jpa-core"))
    implementation(project(":cors-webmvc"))

    // webmvc
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // dbProductFlywayConfig
    runtimeOnly("org.postgresql:postgresql:42.7.4")

    // test
    testImplementation("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
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