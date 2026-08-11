val payment: String by project
val settlement: String by project
val wallet: String by project
val paymentServiceFlyway: String by project

dependencies {
    api(project(payment))
    // api(project(settlement))
    api(project(wallet))
    api(project(paymentServiceFlyway))
}