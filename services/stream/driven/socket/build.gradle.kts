val streamApi: String by project
val streamApplication: String by project

dependencies {
    api(project(streamApi))
    api(project(streamApplication))

    implementation(project(":websocket-core"))

    api(project(":redis-template"))
}
