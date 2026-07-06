val chatApi: String by project
val chatApplication: String by project

dependencies {
    api(project(chatApi))
    api(project(chatApplication))

    implementation("org.springframework:spring-web")
}
