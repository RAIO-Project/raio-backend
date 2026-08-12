val settlementDomain: String by project

dependencies {
    api(project(settlementDomain))
}