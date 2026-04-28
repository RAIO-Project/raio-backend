val core = rootDir.resolve("core")
    .walkTopDown()
    .maxDepth(3)
    .filter(File::isDirectory)
    .associateBy(File::getName)

include(
    ":time-util",
    ":jpa-core",
    ":exception-handler-core",
    ":cors-api",
    ":cors-webmvc",
    ":snowflake-id-api",
    ":snowflake-id-hibernate",
    ":upload-image-api",
    ":upload-image-local",
    ":upload-s3-storage",
    ":batch-api",
    ":batch-core"
)

project(":time-util").projectDir = core["time-util"]!!
project(":jpa-core").projectDir = core["jpa-core"]!!
project(":exception-handler-core").projectDir = core["exception-handler-core"]!!
project(":cors-webmvc").projectDir = core["cors-webmvc"]!!
project(":cors-api").projectDir = core["cors-api"]!!
project(":snowflake-id-api").projectDir = core["snowflake-id-api"]!!
project(":snowflake-id-hibernate").projectDir = core["snowflake-id-hibernate"]!!
project(":upload-image-api").projectDir = core["upload-image-api"]!!
project(":upload-image-local").projectDir = core["upload-image-local"]!!
project(":upload-s3-storage").projectDir = core["upload-s3-storage"]!!
project(":batch-api").projectDir = core["batch-api"]!!
project(":batch-core").projectDir = core["batch-core"]!!
