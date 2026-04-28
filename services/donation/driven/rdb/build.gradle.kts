val donationApplication: String by project
val donationApi: String by project

dependencies {
    val bom = dependencyManagement.importedProperties

    api(project(donationApi))
    api(project(donationApplication))
    api(project(":jpa-core"))
    api(project(":snowflake-id-hibernate"))

    // flyway
    api("org.flywaydb:flyway-core")
    api("org.flywaydb:flyway-database-postgresql")

    // querydsl
    implementation("com.querydsl:querydsl-jpa:${bom["querydsl.version"]}:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:${bom["querydsl.version"]}:jakarta")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // mapstruct
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
}