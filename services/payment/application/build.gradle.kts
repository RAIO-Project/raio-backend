val paymentApi: String by project

dependencies {
    api(project(paymentApi))

    compileOnly("org.springframework:spring-tx")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}