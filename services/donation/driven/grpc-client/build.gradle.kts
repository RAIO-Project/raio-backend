val donationApi: String by project
val donationApplication: String by project
val walletApi: String by project
val grpcClient: String by project

dependencies {
    api(project(donationApi))
    api(project(donationApplication))
    api(project(walletApi))

    implementation(project(grpcClient))
}
