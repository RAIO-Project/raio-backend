val paymentDomain: String by project

dependencies {
    api(project(paymentDomain))
}