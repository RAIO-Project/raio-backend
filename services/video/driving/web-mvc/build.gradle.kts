val videoApi: String by project
val videoApplication: String by project

dependencies {
    api(project(videoApi))
    api(project(videoApplication))
    api(project(":jwt-webmvc"))

    implementation("org.springframework.boot:spring-boot-starter-web")

    compileOnly("jakarta.validation:jakarta.validation-api")
    compileOnly("jakarta.annotation:jakarta.annotation-api")
}
