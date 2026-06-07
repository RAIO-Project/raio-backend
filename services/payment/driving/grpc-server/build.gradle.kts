val paymentApplication: String by project
val paymentApi: String by project
val grpcServer: String by project

dependencies {
    api(project(paymentApi))
    api(project(paymentApplication))

    api(project(grpcServer))
}