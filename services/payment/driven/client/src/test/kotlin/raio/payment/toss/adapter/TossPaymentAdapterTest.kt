package raio.payment.toss.adapter

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.Fault
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import org.springframework.web.client.RestClient
import raio.payment.PaymentReadModels.PaymentConfirmResult
import raio.payment.client.toss.adapter.PaymentTossClientAdapter

class PaymentTossClientAdapterTest : FreeSpec({

    val wireMock = WireMockServer(WireMockConfiguration.options().dynamicPort())
    lateinit var adapter: PaymentTossClientAdapter

    beforeSpec {
        wireMock.start()
        val restClient = RestClient.builder()
            .baseUrl("http://localhost:${wireMock.port()}")
            .defaultHeader("Authorization", "Basic dGVzdDo=")
            .defaultHeader("Content-Type", "application/json")
            .build()
        adapter = PaymentTossClientAdapter(restClient)
    }

    afterSpec {
        wireMock.stop()
    }

    beforeEach {
        wireMock.resetAll()
    }

    "confirm" - {
        "200 응답 시 success 결과를 반환한다" {
            wireMock.stubFor(
                post(urlEqualTo("/v1/payments/confirm"))
                    .withRequestBody(
                        equalToJson("""{"paymentKey":"key-abc","orderId":"order-123","amount":10000}""")
                    )
                    .willReturn(
                        aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(
                                """{"paymentKey":"key-abc","orderId":"order-123","status":"APPROVED","totalAmount":10000,"method":"간편결제"}"""
                            )
                    )
            )

            val result: PaymentConfirmResult = adapter.confirm("key-abc", "order-123", 10_000L)

            result.success shouldBe true
            result.externalTid shouldBe "key-abc"
            result.failMessage shouldBe null
        }

        "4xx 응답 시 failure 결과를 반환한다" {
            wireMock.stubFor(
                post(urlEqualTo("/v1/payments/confirm"))
                    .willReturn(
                        aResponse()
                            .withStatus(400)
                            .withHeader("Content-Type", "application/json")
                            .withBody(
                                """{"code":"ALREADY_PROCESSED_PAYMENT","message":"이미 처리된 결제입니다."}"""
                            )
                    )
            )

            val result: PaymentConfirmResult = adapter.confirm("key-abc", "order-123", 10_000L)

            result.success shouldBe false
            result.externalTid shouldBe null
            result.failMessage.shouldNotBeBlank()
        }

        "네트워크 오류 시 failure 결과를 반환한다" {
            wireMock.stubFor(
                post(urlEqualTo("/v1/payments/confirm"))
                    .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER))
            )

            val result: PaymentConfirmResult = adapter.confirm("key-abc", "order-123", 10_000L)

            result.success shouldBe false
            result.externalTid shouldBe null
        }
    }
})
