val donationApi: String by project
val donationApplication: String by project
val paymentApi: String by project
val grpcClient: String by project

dependencies {
    api(project(donationApi))
    api(project(donationApplication))
    api(project(paymentApi))

    implementation(project(grpcClient))
}
