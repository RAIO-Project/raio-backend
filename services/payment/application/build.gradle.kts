val paymentApi: String by project

dependencies {
    api(project(paymentApi))
}