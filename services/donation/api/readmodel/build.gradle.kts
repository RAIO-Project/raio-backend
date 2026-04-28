val donationDomain: String by project

dependencies {
    api(project(donationDomain))
}