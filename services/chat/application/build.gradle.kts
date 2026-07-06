val chatApi: String by project

dependencies {
    api(project(chatApi))

    compileOnly("org.springframework:spring-tx")
}