val walletApi: String by project
val settlementApi: String by project

dependencies {
    api(project(walletApi))
    api(project(settlementApi))

    compileOnly("org.springframework:spring-tx")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}