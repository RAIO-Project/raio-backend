val paymentApplication: String by project
val walletApi: String by project
val grpcClient: String by project

dependencies {
    api(project(walletApi))
    api(project(paymentApplication))

    implementation(project(grpcClient))
}