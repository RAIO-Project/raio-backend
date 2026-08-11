val paymentApi: String by project
val paymentApplication: String by project

dependencies {
    api(project(paymentApi))
    api(project(paymentApplication))

    implementation("org.springframework.boot:spring-boot-starter-web")

    // configuration properties
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.wiremock:wiremock-standalone:3.3.1")
}
