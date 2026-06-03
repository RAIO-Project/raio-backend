val videoApi: String by project
val videoApplication: String by project
val videoRdbAdapter: String by project
val videoWebMvcAdapter: String by project

dependencies {
    api(project(videoApi))
    api(project(videoApplication))
    api(project(videoRdbAdapter))
    api(project(videoWebMvcAdapter))
}
