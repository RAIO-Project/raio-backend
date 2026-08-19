val settlementApplication: String by project

dependencies {
    api(project(settlementApplication))

    api(project(":batch-scheduler"))
    api(project(":batch-builder"))
}