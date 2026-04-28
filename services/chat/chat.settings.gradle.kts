val chat: String by settings
val chatApi: String by settings
val chatDomain: String by settings
val chatException: String by settings
val chatReadModel: String by settings
val chatApplication: String by settings
val chatRdbAdapter: String by settings
val chatWebMvcAdapter: String by settings
val chatBatchAdapter: String by settings

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

val chatDirectory = getDirectories("services", "chat")

// SERVICE/chat
include(
    chat,
    chatApi,
    chatDomain,
    chatException,
    chatReadModel,
    chatApplication,
    chatRdbAdapter,
    chatWebMvcAdapter,
    // chatBatchAdapter,
)

project(chat).projectDir = chatDirectory("chat")
project(chatApi).projectDir = chatDirectory("api")
project(chatDomain).projectDir = chatDirectory("domain")
project(chatException).projectDir = chatDirectory("exception")
project(chatReadModel).projectDir = chatDirectory("readmodel")
project(chatApplication).projectDir = chatDirectory("application")
project(chatRdbAdapter).projectDir = chatDirectory("rdb")
project(chatWebMvcAdapter).projectDir = chatDirectory("web-mvc")
// project(chatBatchAdapter).projectDir = chatDirectory("batch")