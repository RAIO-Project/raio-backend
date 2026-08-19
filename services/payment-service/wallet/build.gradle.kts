val walletApi: String by project
val walletApplication: String by project
val walletRdbAdapter: String by project
val walletWebMvcAdapter: String by project
val walletGrpcServerAdapter: String by project
val walletGrpcClientAdapter: String by project

dependencies {
    api(project(walletApi))
    api(project(walletApplication))
    api(project(walletRdbAdapter))
    api(project(walletWebMvcAdapter))
    api(project(walletGrpcServerAdapter))
    api(project(walletGrpcClientAdapter))
}
