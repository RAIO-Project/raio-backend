val monolith = rootDir.resolve("monolith")
    .walkTopDown()
    .maxDepth(3)
    .filter(File::isDirectory)
    .associateBy(File::getName)


include(
    ":main-runner",
    ":batch-runner"
)

project(":main-runner").projectDir = monolith["main-runner"]!!
project(":batch-runner").projectDir = monolith["batch-runner"]!!