val wallet: String by settings
val walletApi: String by settings
val walletDomain: String by settings
val walletException: String by settings
val walletReadModel: String by settings
val walletApplication: String by settings
val walletRdbAdapter: String by settings
val walletWebMvcAdapter: String by settings
val walletProto: String by settings
val walletGrpcServerAdapter: String by settings

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

val walletDirectory = getDirectories("services/payment-service", "wallet")

// SERVICE/wallet
include(
    wallet,
    walletApi,
    walletDomain,
    walletException,
    walletReadModel,
    walletApplication,
    walletRdbAdapter,
    walletWebMvcAdapter,
    walletProto,
    walletGrpcServerAdapter,
)

project(wallet).projectDir = walletDirectory("wallet")
project(walletApi).projectDir = walletDirectory("api")
project(walletDomain).projectDir = walletDirectory("domain")
project(walletException).projectDir = walletDirectory("exception")
project(walletReadModel).projectDir = walletDirectory("readmodel")
project(walletApplication).projectDir = walletDirectory("application")
project(walletRdbAdapter).projectDir = walletDirectory("rdb")
project(walletWebMvcAdapter).projectDir = walletDirectory("web-mvc")
project(walletProto).projectDir = walletDirectory("proto")
project(walletGrpcServerAdapter).projectDir = walletDirectory("grpc-server")