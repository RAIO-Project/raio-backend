val settlementDomain: String by project
val settlementException: String by project
val settlementReadModel: String by project

dependencies {
    api(project(settlementDomain))
    api(project(settlementException))
    api(project(settlementReadModel))
}
