val settlementApplication: String by project
val grpcServer: String by project

dependencies {
    api(project(settlementApplication))
    api(project(grpcServer))
}