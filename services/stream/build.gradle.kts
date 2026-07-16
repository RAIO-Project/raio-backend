val streamApi: String by project
val streamApplication: String by project
val streamRdbAdapter: String by project
val streamRedisAdapter: String by project
val streamSocketAdapter: String by project
val streamWebSocketAdapter: String by project
val streamWebMvcAdapter: String by project
val streamWebSocketAdapter: String by project

dependencies {
    api(project(streamApi))
    api(project(streamApplication))
    api(project(streamRdbAdapter))
    api(project(streamRedisAdapter))
    api(project(streamSocketAdapter))
    api(project(streamWebSocketAdapter))
    api(project(streamWebMvcAdapter))
    api(project(streamWebSocketAdapter))
}