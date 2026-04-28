val user: String by settings
val userApi: String by settings
val userDomain: String by settings
val userException: String by settings
val userReadModel: String by settings
val userApplication: String by settings
val userRdbAdapter: String by settings
val userWebMvcAdapter: String by settings
val userBatchAdapter: String by settings

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

val userDirectory = getDirectories("services", "user")

// SERVICE/user
include(
    user,
    userApi,
    userDomain,
    userException,
    userReadModel,
    userApplication,
    userRdbAdapter,
    userWebMvcAdapter,
    // userBatchAdapter,
)

project(user).projectDir = userDirectory("user")
project(userApi).projectDir = userDirectory("api")
project(userDomain).projectDir = userDirectory("domain")
project(userException).projectDir = userDirectory("exception")
project(userReadModel).projectDir = userDirectory("readmodel")
project(userApplication).projectDir = userDirectory("application")
project(userRdbAdapter).projectDir = userDirectory("rdb")
project(userWebMvcAdapter).projectDir = userDirectory("web-mvc")
// project(userBatchAdapter).projectDir = userDirectory("batch")