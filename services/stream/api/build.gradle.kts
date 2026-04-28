val streamDomain: String by project
val streamException: String by project
val streamReadModel: String by project

dependencies {
    api(project(streamDomain))
    api(project(streamException))
    api(project(streamReadModel))
}