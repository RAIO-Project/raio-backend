rootProject.name = "raio-backend"

val services = "${rootProject.projectDir}/services"
//
apply(from = "common/common.settings.gradle.kts")
apply(from = "core/core.settings.gradle.kts")
apply(from = "monolith/monolith.settings.gradle.kts")

// services
apply(from = "$services/chat/chat.settings.gradle.kts")
apply(from = "$services/donation/donation.settings.gradle.kts")
apply(from = "$services/payment/payment.settings.gradle.kts")
apply(from = "$services/stream/stream.settings.gradle.kts")
apply(from = "$services/user/user.settings.gradle.kts")
