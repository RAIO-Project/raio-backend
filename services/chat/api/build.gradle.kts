val chatDomain: String by project
val chatException: String by project
val chatReadModel: String by project

dependencies {
    api(project(chatDomain))
    api(project(chatException))
    api(project(chatReadModel))
}