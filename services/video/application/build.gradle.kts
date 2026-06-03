val videoApi: String by project

dependencies {
    api(project(videoApi))
    api(project(":upload-file-api"))
}
