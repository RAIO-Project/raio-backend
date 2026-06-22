val donationApi: String by project
val donationApplication: String by project

dependencies {
    api(project(donationApi))
    api(project(donationApplication))

    implementation(project(":websocket-core"))

    api(project(":redis-template"))
}