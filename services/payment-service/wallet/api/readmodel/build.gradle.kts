val walletDomain: String by project

dependencies {
    api(project(walletDomain))
}