val paymentApi: String by project
val walletApi: String by project

dependencies {
    api(project(paymentApi))
    api(project(walletApi))

    compileOnly("org.springframework:spring-tx")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}