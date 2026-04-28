val streamApi: String by project
val streamApplication: String by project
val streamRdbAdapter: String by project
val streamWebMvcAdapter: String by project

dependencies {
    api(project(streamApi))
    api(project(streamApplication))
    api(project(streamRdbAdapter))
    api(project(streamWebMvcAdapter))
}