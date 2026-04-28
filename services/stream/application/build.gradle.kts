val stream: String by project

dependencies {
    api(project(stream))
}