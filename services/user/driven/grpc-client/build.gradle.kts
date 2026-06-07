val userApplication: String by project
val userApi: String by project
val paymentApi: String by project
val grpcClient: String by project

dependencies {
    api(project(userApi))
    api(project(userApplication))
    api(project(paymentApi))

    implementation(project(grpcClient))
}