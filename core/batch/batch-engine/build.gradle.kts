dependencies{
    api(project(":batch-api"))

    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.batch:spring-batch-core")

    testImplementation("org.springframework.boot:spring-boot-autoconfigure")
    testImplementation("org.springframework.batch:spring-batch-core")
}
