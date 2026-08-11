val paymentService: String by settings
val paymentServiceFlyway: String by settings
val payment: String by settings
val wallet: String by settings
val settlement: String by settings

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

val paymentDirectory = getDirectories("services", "payment-service")

// SERVICE/payment-service
include(
    paymentService,
    paymentServiceFlyway,
    payment,
    wallet,
    settlement,
)

project(paymentService).projectDir = paymentDirectory("payment-service")
project(paymentServiceFlyway).projectDir = paymentDirectory("payment-service-flyway")
project(payment).projectDir = paymentDirectory("payment")
project(settlement).projectDir = paymentDirectory("settlement")
project(wallet).projectDir = paymentDirectory("wallet")

val paymentServiceDir = rootDir.resolve("services/payment-service")

apply(from = paymentServiceDir.resolve("payment/payment.settings.gradle.kts"))
apply(from = paymentServiceDir.resolve("wallet/wallet.settings.gradle.kts"))
// apply(from = paymentServiceDir.resolve("settlement/settlement.settings.gradle.kts"))