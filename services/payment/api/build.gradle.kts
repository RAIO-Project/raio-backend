val paymentDomain: String by project
val paymentException: String by project
val paymentReadModel: String by project
val paymentProto: String by project

dependencies {
    api(project(paymentDomain))
    api(project(paymentException))
    api(project(paymentReadModel))
    api(project(paymentProto))
}