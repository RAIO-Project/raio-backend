val paymentApi: String by project

dependencies {
    api(project(paymentApi))

    compileOnly("org.springframework:spring-tx")
}