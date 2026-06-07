val chatApplication: String by project
val chatApi: String by project

dependencies {
    api(project(chatApi))
    api(project(chatApplication))

    implementation("org.springframework:spring-messaging")
    implementation("org.springframework:spring-websocket")

    // Redis pub/sub
    api(project(":redis-api"))
}