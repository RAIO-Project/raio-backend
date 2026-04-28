val donationApi: String by project
val donationApplication: String by project
val donationRdbAdapter: String by project
val donationWebMvcAdapter: String by project

dependencies {
    api(project(donationApi))
    api(project(donationApplication))
    api(project(donationRdbAdapter))
    api(project(donationWebMvcAdapter))
}