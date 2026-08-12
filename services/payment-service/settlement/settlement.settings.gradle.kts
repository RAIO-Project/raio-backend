val settlement: String by settings
val settlementApi: String by settings
val settlementDomain: String by settings
val settlementException: String by settings
val settlementReadModel: String by settings
val settlementApplication: String by settings
val settlementRdbAdapter: String by settings
val settlementWebMvcAdapter: String by settings
val settlementBatchAdapter: String by settings
val settlementGrpcClientAdapter: String by settings

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

val settlementDirectory = getDirectories("services/payment-service", "settlement")

// SERVICE/settlement
include(
    settlement,
    settlementApi,
    settlementDomain,
    settlementException,
    settlementReadModel,
    settlementApplication,
    settlementRdbAdapter,
    settlementWebMvcAdapter,
    settlementBatchAdapter,
    settlementGrpcClientAdapter,
)

project(settlement).projectDir = settlementDirectory("settlement")
project(settlementApi).projectDir = settlementDirectory("api")
project(settlementDomain).projectDir = settlementDirectory("domain")
project(settlementException).projectDir = settlementDirectory("exception")
project(settlementReadModel).projectDir = settlementDirectory("readmodel")
project(settlementApplication).projectDir = settlementDirectory("application")
project(settlementRdbAdapter).projectDir = settlementDirectory("rdb")
project(settlementWebMvcAdapter).projectDir = settlementDirectory("web-mvc")
project(settlementBatchAdapter).projectDir = settlementDirectory("batch")
project(settlementGrpcClientAdapter).projectDir = settlementDirectory("grpc-client")