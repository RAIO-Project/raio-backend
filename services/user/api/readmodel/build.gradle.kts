val userDomain: String by project

dependencies {
    api(project(userDomain))
}