val chatApi: String by project
val chatApplication: String by project
val chatRdbAdapter: String by project
val chatWebMvcAdapter: String by project

dependencies {
    api(project(chatApi))
    api(project(chatApplication))
    api(project(chatRdbAdapter))
    api(project(chatWebMvcAdapter))
}