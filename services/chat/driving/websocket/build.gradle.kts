val chatApplication: String by project
val chatApi: String by project

dependencies {
    api(project(chatApi))
    api(project(chatApplication))

    // STOMP over WebSocket
    implementation("org.springframework:spring-messaging")
    implementation(project(":websocket-core"))
    // validation
    compileOnly("jakarta.validation:jakarta.validation-api")

    // mapstruct
    compileOnly("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
}