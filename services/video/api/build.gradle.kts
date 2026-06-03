val videoDomain: String by project
val videoException: String by project

dependencies {
    api(project(videoDomain))
    api(project(videoException))
}
