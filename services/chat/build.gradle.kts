val chatApi: String by project
val chatApplication: String by project
val chatRdbAdapter: String by project
val chatWebMvcAdapter: String by project
val chatWebSocketAdapter: String by project
val chatSocketAdapter: String by project
val chatModerationAdapter: String by project
val chatHuggingFaceAdapter: String by project

dependencies {
    api(project(chatApi))
    api(project(chatApplication))
    api(project(chatRdbAdapter))
    api(project(chatWebMvcAdapter))
    api(project(chatWebSocketAdapter))
    api(project(chatSocketAdapter))
    api(project(chatModerationAdapter))
    api(project(chatHuggingFaceAdapter))
}