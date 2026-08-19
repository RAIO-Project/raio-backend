val settlementApplication: String by project
val grpcClient: String by project

dependencies {
    api(project(settlementApplication))
    api(project(grpcClient))
}