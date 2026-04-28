val userApi: String by project
val userApplication: String by project
val userRdbAdapter: String by project
val userWebMvcAdapter: String by project

dependencies {
    api(project(userApi))
    api(project(userApplication))
    api(project(userRdbAdapter))
    api(project(userWebMvcAdapter))
}