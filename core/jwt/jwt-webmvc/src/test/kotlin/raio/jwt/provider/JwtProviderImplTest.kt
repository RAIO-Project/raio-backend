package raio.jwt.provider

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import raio.jwt.properties.JwtProperties

class JwtProviderImplTest : DescribeSpec({

    // Base64 인코딩된 테스트용 시크릿 키 (32바이트 이상 필요)
    val testSecretKey = "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RpbmctcHVycG9zZXMhIQ=="
    val properties = JwtProperties(testSecretKey, 1800, 1209600)
    val jwtProvider = JwtProviderImpl(properties)

    val userId = "user-123"
    val roles = setOf("USER")

    describe("generate()") {
        it("accessToken과 refreshToken을 모두 반환한다") {
            val tokenPair = jwtProvider.generate(userId, roles)

            tokenPair.accessToken().shouldNotBeEmpty()
            tokenPair.refreshToken().shouldNotBeEmpty()
        }

        it("accessToken과 refreshToken은 서로 다르다") {
            val tokenPair = jwtProvider.generate(userId, roles)

            tokenPair.accessToken() shouldNotBe tokenPair.refreshToken()
        }
    }

    describe("validate()") {
        it("정상 토큰은 true를 반환한다") {
            val token = jwtProvider.generate(userId, roles).accessToken()

            jwtProvider.validate(token) shouldBe true
        }

        it("변조된 토큰은 false를 반환한다") {
            val token = jwtProvider.generate(userId, roles).accessToken()
            val tampered = token.dropLast(5) + "XXXXX"

            jwtProvider.validate(tampered) shouldBe false
        }

        it("빈 문자열은 false를 반환한다") {
            jwtProvider.validate("") shouldBe false
        }

        it("랜덤 문자열은 false를 반환한다") {
            jwtProvider.validate("not.a.jwt.token") shouldBe false
        }
    }

    describe("extractUserId()") {
        it("토큰에서 userId를 정확히 추출한다") {
            val token = jwtProvider.generate(userId, roles).accessToken()

            jwtProvider.extractUserId(token) shouldBe userId
        }
    }

    describe("extractRoles()") {
        it("토큰에서 roles를 정확히 추출한다") {
            val token = jwtProvider.generate(userId, roles).accessToken()

            jwtProvider.extractRoles(token) shouldContain "USER"
        }

        it("여러 roles도 모두 추출한다") {
            val multiRoles = setOf("USER", "ADMIN")
            val token = jwtProvider.generate(userId, multiRoles).accessToken()
            val extracted = jwtProvider.extractRoles(token)

            extracted shouldContain "USER"
            extracted shouldContain "ADMIN"
        }
    }
})