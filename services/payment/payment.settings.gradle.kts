val payment: String by settings
val paymentApi: String by settings
val paymentDomain: String by settings
val paymentException: String by settings
val paymentReadModel: String by settings
val paymentApplication: String by settings
val paymentRdbAdapter: String by settings
val paymentWebMvcAdapter: String by settings
val paymentBatchAdapter: String by settings

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

val paymentDirectory = getDirectories("services", "payment")

// SERVICE/payment
include(
    payment,
    paymentApi,
    paymentDomain,
    paymentException,
    paymentReadModel,
    paymentApplication,
    paymentRdbAdapter,
    paymentWebMvcAdapter,
    // paymentBatchAdapter,
)

project(payment).projectDir = paymentDirectory("payment")
project(paymentApi).projectDir = paymentDirectory("api")
project(paymentDomain).projectDir = paymentDirectory("domain")
project(paymentException).projectDir = paymentDirectory("exception")
project(paymentReadModel).projectDir = paymentDirectory("readmodel")
project(paymentApplication).projectDir = paymentDirectory("application")
project(paymentRdbAdapter).projectDir = paymentDirectory("rdb")
project(paymentWebMvcAdapter).projectDir = paymentDirectory("web-mvc")
// project(paymentBatchAdapter).projectDir = paymentDirectory("batch")