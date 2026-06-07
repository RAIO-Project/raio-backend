val grpcClient: String by project
val grpcServer: String by project

dependencies {
    api(project(grpcClient))
    api(project(grpcServer))
}