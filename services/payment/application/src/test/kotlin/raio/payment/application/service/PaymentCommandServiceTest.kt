package raio.payment.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import raio.payment.PaymentReadModels.PaymentConfirmResult
import raio.payment.application.command.PaymentCommands.ConfirmCommand
import raio.payment.application.command.PaymentCommands.PrepareCommand
import raio.payment.application.port.PaymentClientPort
import raio.payment.application.port.PaymentCommandRepositoryPort
import raio.payment.application.usecase.PointChargeUseCase
import raio.payment.application.usecase.WalletReadUseCase
import raio.payment.domain.Payment
import raio.payment.domain.Wallet
import raio.payment.domain.type.PaymentMethod
import raio.payment.domain.type.PaymentStatus
import raio.payment.domain.type.PgProvider
import raio.payment.exception.PaymentException
import java.util.Optional
import java.util.function.Supplier

class PaymentCommandServiceTest : FreeSpec({

    val walletReadUseCase = mockk<WalletReadUseCase>()
    val pointChargeUseCase = mockk<PointChargeUseCase>()
    val paymentCommandRepositoryPort = mockk<PaymentCommandRepositoryPort>()
    val paymentClientPort = mockk<PaymentClientPort>()

    val service = PaymentCommandService(
        walletReadUseCase,
        pointChargeUseCase,
        paymentCommandRepositoryPort,
        paymentClientPort,
    )

    beforeEach {
        // transaction()이 Supplier를 실제로 실행하도록 설정한다.
        every { paymentCommandRepositoryPort.transaction<Any>(any()) } answers {
            firstArg<Supplier<Any>>().get()
        }
    }

    afterEach { clearAllMocks() }

    fun readyPayment(
        id: String = "1",
        userId: String = "user-1",
        amount: Long = 10_000L,
        orderId: String = "order-123",
    ): Payment = Payment.builder()
        .id(id).userId(userId).amount(amount)
        .status(PaymentStatus.READY).orderId(orderId)
        .build()

    "prepare" - {
        "READY 상태의 Payment를 생성하여 저장한다" {
            val command = PrepareCommand("user-1", 10_000L, PaymentMethod.EASY_PAY, PgProvider.TOSS)
            val saved = Payment.builder()
                .id("1").orderId("abc123").userId("user-1").amount(10_000L)
                .status(PaymentStatus.READY).method(PaymentMethod.EASY_PAY).pgProvider(PgProvider.TOSS)
                .build()

            every { paymentCommandRepositoryPort.save(any()) } returns saved

            val result = service.prepare(command)

            result.status shouldBe PaymentStatus.READY
            result.userId shouldBe "user-1"
            result.amount shouldBe 10_000L
            result.orderId shouldNotBe null
            verify(exactly = 1) { paymentCommandRepositoryPort.save(any()) }
        }
    }

    "confirm" - {
        "성공: READY → APPROVING → PG 승인 → APPROVED + 지갑 충전" {
            val command = ConfirmCommand("1", "toss-key", "order-123", 10_000L)
            val approving = Payment.builder().id("1").userId("user-1").amount(10_000L)
                .orderId("order-123").status(PaymentStatus.APPROVING).build()
            val approved = Payment.builder().id("1").userId("user-1").status(PaymentStatus.APPROVED).build()
            val wallet = Wallet.builder().id("w-1").userId("user-1").balance(5_000L).build()
            val charged = Wallet.builder().id("w-1").userId("user-1").balance(15_000L).build()

            every { paymentCommandRepositoryPort.findByIdForUpdate("1") } returns Optional.of(readyPayment())
            every { paymentCommandRepositoryPort.updateStatus("1", PaymentStatus.APPROVING, null, null) } returns Optional.of(approving)
            every { paymentClientPort.confirm("toss-key", "order-123", 10_000L) } returns PaymentConfirmResult.success("ext-tid")
            every { paymentCommandRepositoryPort.updateStatus("1", PaymentStatus.APPROVED, "ext-tid", null) } returns Optional.of(approved)
            every { walletReadUseCase.getWallet("user-1") } returns wallet
            every { pointChargeUseCase.charge("w-1", 10_000L) } returns charged

            val result = service.confirm(command)

            result.status shouldBe PaymentStatus.APPROVED
            verify(exactly = 1) { paymentClientPort.confirm("toss-key", "order-123", 10_000L) }
            verify(exactly = 1) { walletReadUseCase.getWallet("user-1") }
            verify(exactly = 1) { pointChargeUseCase.charge("w-1", 10_000L) }
        }

        "실패: PG 승인 거절 시 FAILED 확정 후 지갑을 충전하지 않는다" {
            val command = ConfirmCommand("1", "toss-key", "order-123", 10_000L)
            val approving = Payment.builder().id("1").userId("user-1").amount(10_000L)
                .orderId("order-123").status(PaymentStatus.APPROVING).build()
            val failed = Payment.builder().id("1").status(PaymentStatus.FAILED).build()

            every { paymentCommandRepositoryPort.findByIdForUpdate("1") } returns Optional.of(readyPayment())
            every { paymentCommandRepositoryPort.updateStatus("1", PaymentStatus.APPROVING, null, null) } returns Optional.of(approving)
            every { paymentClientPort.confirm("toss-key", "order-123", 10_000L) } returns PaymentConfirmResult.failure("카드사 승인 거절")
            every { paymentCommandRepositoryPort.updateStatus("1", PaymentStatus.FAILED, null, "카드사 승인 거절") } returns Optional.of(failed)

            val result = service.confirm(command)

            result.status shouldBe PaymentStatus.FAILED
            verify(exactly = 0) { walletReadUseCase.getWallet(any()) }
            verify(exactly = 0) { pointChargeUseCase.charge(any(), any()) }
        }

        "READY가 아닌 결제 confirm 시 예외를 던진다" {
            val command = ConfirmCommand("1", "toss-key", "order-123", 10_000L)
            val alreadyApproving = Payment.builder().id("1").status(PaymentStatus.APPROVING)
                .orderId("order-123").amount(10_000L).build()

            every { paymentCommandRepositoryPort.findByIdForUpdate("1") } returns Optional.of(alreadyApproving)

            shouldThrow<PaymentException> { service.confirm(command) }
            verify(exactly = 0) { paymentClientPort.confirm(any(), any(), any()) }
        }

        "금액 불일치 시 예외를 던진다" {
            val command = ConfirmCommand("1", "toss-key", "order-123", 9_999L)

            every { paymentCommandRepositoryPort.findByIdForUpdate("1") } returns Optional.of(readyPayment())

            shouldThrow<PaymentException> { service.confirm(command) }
            verify(exactly = 0) { paymentClientPort.confirm(any(), any(), any()) }
        }

        "orderId 불일치 시 예외를 던진다" {
            val command = ConfirmCommand("1", "toss-key", "wrong-order", 10_000L)

            every { paymentCommandRepositoryPort.findByIdForUpdate("1") } returns Optional.of(readyPayment())

            shouldThrow<PaymentException> { service.confirm(command) }
            verify(exactly = 0) { paymentClientPort.confirm(any(), any(), any()) }
        }

        "결제를 찾을 수 없으면 예외를 던진다" {
            val command = ConfirmCommand("999", "toss-key", "order-123", 10_000L)

            every { paymentCommandRepositoryPort.findByIdForUpdate("999") } returns Optional.empty()

            shouldThrow<PaymentException> { service.confirm(command) }
        }
    }
})
