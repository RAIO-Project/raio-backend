val donationApplication: String by project
val donationApi: String by project

val grpcServer: String by project

dependencies {
    api(project(donationApi))
    api(project(donationApplication))


    api(project(grpcServer))
}
