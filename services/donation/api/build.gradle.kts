val donationDomain: String by project
val donationException: String by project
val donationReadModel: String by project

dependencies {
    api(project(donationDomain))
    api(project(donationException))
    api(project(donationReadModel))
}