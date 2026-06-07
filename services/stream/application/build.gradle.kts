val streamApi: String by project

dependencies {
    api(project(streamApi))
    api(project(":upload-file-api"))

    // spring
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.springframework:spring-tx")
}