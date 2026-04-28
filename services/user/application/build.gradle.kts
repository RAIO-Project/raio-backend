val userApi: String by project

dependencies {
    api(project(userApi))
}