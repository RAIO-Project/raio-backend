val donation: String by settings
val donationApi: String by settings
val donationDomain: String by settings
val donationException: String by settings
val donationReadModel: String by settings
val donationApplication: String by settings
val donationRdbAdapter: String by settings
val donationWebMvcAdapter: String by settings
val donationBatchAdapter: String by settings

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

val donationDirectory = getDirectories("services", "donation")

// SERVICE/donation
include(
    donation,
    donationApi,
    donationDomain,
    donationException,
    donationReadModel,
    donationApplication,
    donationRdbAdapter,
    donationWebMvcAdapter,
    // donationBatchAdapter,
)

project(donation).projectDir = donationDirectory("donation")
project(donationApi).projectDir = donationDirectory("api")
project(donationDomain).projectDir = donationDirectory("domain")
project(donationException).projectDir = donationDirectory("exception")
project(donationReadModel).projectDir = donationDirectory("readmodel")
project(donationApplication).projectDir = donationDirectory("application")
project(donationRdbAdapter).projectDir = donationDirectory("rdb")
project(donationWebMvcAdapter).projectDir = donationDirectory("web-mvc")
// project(donationBatchAdapter).projectDir = donationDirectory("batch")