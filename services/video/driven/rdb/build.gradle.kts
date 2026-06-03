val videoApi: String by project
val videoApplication: String by project

dependencies {
    val bom = dependencyManagement.importedProperties

    api(project(videoApi))
    api(project(videoApplication))
    api(project(":jpa-core"))
    api(project(":snowflake-id-hibernate"))
    api(project(":upload-file-local"))

    // flyway
    api("org.flywaydb:flyway-core")
    api("org.flywaydb:flyway-database-postgresql")

    // mapstruct
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
}
