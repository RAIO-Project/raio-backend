val chatDomain: String by project

dependencies {
    api(project(chatDomain))
}