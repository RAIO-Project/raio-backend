val stream: String by settings
val streamApi: String by settings
val streamDomain: String by settings
val streamException: String by settings
val streamReadModel: String by settings
val streamApplication: String by settings
val streamRdbAdapter: String by settings
val streamWebMvcAdapter: String by settings
val streamBatchAdapter: String by settings

fun getDirectories(vararg names: String): (String) -> File {
    var dir = rootDir
    for (name in names) {
        dir = dir.resolve(name)
    }
    return { targetName ->
        val directory = dir.walkTopDown().maxDepth(3)
            .filter(File::isDirectory)
            .associateBy { it.name }
        directory[targetName] ?: throw Error("그런 폴더가 없습니다: $targetName")
    }
}

val streamDirectory = getDirectories("services", "stream")

// SERVICE/stream
include(
    stream,
    streamApi,
    streamDomain,
    streamException,
    streamReadModel,
    streamApplication,
    streamRdbAdapter,
    streamWebMvcAdapter,
    // streamBatchAdapter,
)

project(stream).projectDir = streamDirectory("stream")
project(streamApi).projectDir = streamDirectory("api")
project(streamDomain).projectDir = streamDirectory("domain")
project(streamException).projectDir = streamDirectory("exception")
project(streamReadModel).projectDir = streamDirectory("readmodel")
project(streamApplication).projectDir = streamDirectory("application")
project(streamRdbAdapter).projectDir = streamDirectory("rdb")
project(streamWebMvcAdapter).projectDir = streamDirectory("web-mvc")
// project(streamBatchAdapter).projectDir = streamDirectory("batch")