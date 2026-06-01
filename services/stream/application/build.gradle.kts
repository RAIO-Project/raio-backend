val streamApi: String by project

dependencies {
    api(project(streamApi))

    // spring
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.springframework:spring-tx")
}