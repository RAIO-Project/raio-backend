val paymentApplication: String by project
val paymentApi: String by project

dependencies {
    api(project(paymentApplication))

    api(project(":batch-scheduler"))
    api(project(":batch-builder"))
}