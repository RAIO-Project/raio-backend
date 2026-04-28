val userDomain: String by project
val userException: String by project
val userReadModel: String by project

dependencies {
    api(project(userDomain))
    api(project(userException))
    api(project(userReadModel))
}