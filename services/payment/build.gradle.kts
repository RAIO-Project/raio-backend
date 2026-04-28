val paymentApi: String by project
val paymentApplication: String by project
val paymentRdbAdapter: String by project
val paymentWebMvcAdapter: String by project

dependencies {
    api(project(paymentApi))
    api(project(paymentApplication))
    api(project(paymentRdbAdapter))
    api(project(paymentWebMvcAdapter))
}