val chatApplication: String by project
val chatApi: String by project

dependencies {
    api(project(chatApi))
    api(project(chatApplication))

    // WebSocket broadcast
    implementation("org.springframework:spring-messaging")
    implementation("org.springframework:spring-websocket")
}