val walletApi: String by project
val walletApplication: String by project
val grpcServer: String by project

dependencies {
    api(project(walletApi))
    api(project(walletApplication))

    api(project(grpcServer))
}