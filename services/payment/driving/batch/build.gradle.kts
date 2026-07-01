val paymentApplication: String by project
val paymentApi: String by project

dependencies {
    api(project(paymentApplication))

    api(":batch-scheduler")
    api(":batch-builder")
}