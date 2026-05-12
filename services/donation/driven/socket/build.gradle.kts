val donationApi: String by project
val donationApplication: String by project

dependencies {
    api(project(donationApi))
    api(project(donationApplication))
    api(project(":websocket-core"))
    implementation("org.springframework:spring-messaging")
    implementation("org.springframework:spring-websocket")
}