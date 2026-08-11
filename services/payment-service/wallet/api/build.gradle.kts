val walletDomain: String by project
val walletException: String by project
val walletProto: String by project
val walletReadModel: String by project

dependencies {
    api(project(walletDomain))
    api(project(walletException))
    api(project(walletProto))
    api(project(walletReadModel))
}