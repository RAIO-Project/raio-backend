val chatApplication: String by project
val chatApi: String by project

dependencies {
    api(project(chatApi))
    api(project(chatApplication))

    implementation(project(":websocket-core"))

    api(project(":redis-template"))
}