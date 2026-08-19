val walletApplication: String by project
val grpcClient: String by project

dependencies {
    api(project(walletApplication))
    api(project(grpcClient))
}