import org.springframework.boot.gradle.tasks.bundling.BootJar

val paymentBatchAdapter: String by project
val paymentRdbAdapter: String by project
val paymentClientAdapter: String by project

version = "0.0.1-SNAPSHOT"

dependencies {
    // service
    api(project(paymentBatchAdapter))
    api(project(paymentRdbAdapter))
    api(project(paymentClientAdapter))

    implementation("org.springframework.boot:spring-boot-starter-batch")

    // core
    api(project(":batch-engine"))
    api(project(":batch-dashboard-support"))

    // db
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
