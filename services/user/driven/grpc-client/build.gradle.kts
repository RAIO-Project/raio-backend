val userApplication: String by project
val userApi: String by project
val walletApi: String by project
val grpcClient: String by project

dependencies {
    api(project(userApi))
    api(project(userApplication))
    api(project(walletApi))

    implementation(project(grpcClient))
}