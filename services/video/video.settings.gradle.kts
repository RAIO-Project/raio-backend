val video: String by settings
val videoApi: String by settings
val videoDomain: String by settings
val videoException: String by settings
val videoApplication: String by settings
val videoRdbAdapter: String by settings
val videoWebMvcAdapter: String by settings

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

val videoDirectory = getDirectories("services", "video")

include(
    video,
    videoApi,
    videoDomain,
    videoException,
    videoApplication,
    videoRdbAdapter,
    videoWebMvcAdapter,
)

project(video).projectDir = videoDirectory("video")
project(videoApi).projectDir = videoDirectory("api")
project(videoDomain).projectDir = videoDirectory("domain")
project(videoException).projectDir = videoDirectory("exception")
project(videoApplication).projectDir = videoDirectory("application")
project(videoRdbAdapter).projectDir = videoDirectory("rdb")
project(videoWebMvcAdapter).projectDir = videoDirectory("web-mvc")
