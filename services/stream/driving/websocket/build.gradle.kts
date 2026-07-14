val streamApi: String by project
val streamApplication: String by project

dependencies {
    api(project(streamApi))
    api(project(streamApplication))

    implementation("org.springframework:spring-messaging")
    implementation(project(":websocket-core"))
}
