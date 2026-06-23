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
    ":jwt-api",
    ":jwt-webmvc",
    ":snowflake-id-api",
    ":snowflake-id-hibernate",
    ":upload-image-api",
    ":upload-image-local",
    ":upload-s3-storage",
    ":upload-file-api",
    ":batch-api",
    ":batch-core",
    ":websocket-core",
    ":redis-api",
    ":redis-template",
    ":redis-cache",
    ":grpc",
    ":grpc:grpc-server",
    ":grpc:grpc-client",
    ":monitoring-core",
)

project(":time-util").projectDir = core["time-util"]!!
project(":jpa-core").projectDir = core["jpa-core"]!!
project(":exception-handler-core").projectDir = core["exception-handler-core"]!!
project(":cors-webmvc").projectDir = core["cors-webmvc"]!!
project(":cors-api").projectDir = core["cors-api"]!!
project(":jwt-api").projectDir = core["jwt-api"]!!
project(":jwt-webmvc").projectDir = core["jwt-webmvc"]!!
project(":snowflake-id-api").projectDir = core["snowflake-id-api"]!!
project(":snowflake-id-hibernate").projectDir = core["snowflake-id-hibernate"]!!
project(":upload-image-api").projectDir = core["upload-image-api"]!!
project(":upload-image-local").projectDir = core["upload-image-local"]!!
project(":upload-s3-storage").projectDir = core["upload-s3-storage"]!!
project(":upload-file-api").projectDir = core["upload-file-api"]!!
project(":batch-api").projectDir = core["batch-api"]!!
project(":batch-core").projectDir = core["batch-core"]!!
project(":websocket-core").projectDir = core["websocket-core"]!!
project(":redis-api").projectDir = core["redis-api"]!!
project(":redis-template").projectDir = core["redis-template"]!!
project(":redis-cache").projectDir = core["redis-cache"]!!
project(":grpc").projectDir = core["grpc"]!!
project(":grpc:grpc-client").projectDir = core["grpc-client"]!!
project(":grpc:grpc-server").projectDir = core["grpc-server"]!!
project(":monitoring-core").projectDir = core["monitoring-core"]!!
