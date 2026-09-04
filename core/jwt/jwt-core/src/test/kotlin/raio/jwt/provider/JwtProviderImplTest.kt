package raio.jwt.provider

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import raio.jwt.TokenType
import raio.jwt.properties.JwtProperties

class JwtProviderImplTest : DescribeSpec({

    // Base64 인코딩된 테스트용 시크릿 키 (32바이트 이상 필요)
    val testSecretKey = "dGVzdC1zZWNyZXQta2V5LWZvci11bml0LXRlc3RpbmctcHVycG9zZXMhIQ=="
    val properties = JwtProperties(testSecretKey, 1800, 1209600)
    val jwtProvider = JwtProviderImpl(properties)

    val userId = "user-123"
    val nickName = "테스터"
    val roles = setOf("USER")

    describe("generate()") {
        it("accessToken과 refreshToken을 모두 반환한다") {
            val tokenPair = jwtProvider.generate(userId, nickName, roles)

            tokenPair.accessToken().shouldNotBeEmpty()
            tokenPair.refreshToken().shouldNotBeEmpty()
        }

        it("accessToken과 refreshToken은 서로 다르다") {
            val tokenPair = jwtProvider.generate(userId, nickName, roles)

            tokenPair.accessToken() shouldNotBe tokenPair.refreshToken()
        }
    }

    describe("validate() — 용도 구분") {
        it("accessToken은 ACCESS로 검증하면 통과한다") {
            val token = jwtProvider.generate(userId, nickName, roles).accessToken()

            jwtProvider.validate(token, TokenType.ACCESS) shouldBe true
        }

        it("refreshToken은 REFRESH로 검증하면 통과한다") {
            val token = jwtProvider.generate(userId, nickName, roles).refreshToken()

            jwtProvider.validate(token, TokenType.REFRESH) shouldBe true
        }

        // 이 두 케이스가 이번 수정의 핵심이다.
        // 예전에는 두 토큰의 클레임이 동일해 서로 대체 사용이 가능했다.
        it("refreshToken을 ACCESS로 검증하면 거부한다") {
            val token = jwtProvider.generate(userId, nickName, roles).refreshToken()

            jwtProvider.validate(token, TokenType.ACCESS) shouldBe false
        }

        it("accessToken으로는 재발급할 수 없다") {
            val token = jwtProvider.generate(userId, nickName, roles).accessToken()

            jwtProvider.validate(token, TokenType.REFRESH) shouldBe false
        }
    }

    describe("validate() — 서명·형식") {
        it("변조된 토큰은 false를 반환한다") {
            val token = jwtProvider.generate(userId, nickName, roles).accessToken()
            val tampered = token.dropLast(5) + "XXXXX"

            jwtProvider.validate(tampered, TokenType.ACCESS) shouldBe false
        }

        it("빈 문자열은 false를 반환한다") {
            jwtProvider.validate("", TokenType.ACCESS) shouldBe false
        }

        it("랜덤 문자열은 false를 반환한다") {
            jwtProvider.validate("not.a.jwt.token", TokenType.ACCESS) shouldBe false
        }
    }

    describe("extractUserId()") {
        it("토큰에서 userId를 정확히 추출한다") {
            val token = jwtProvider.generate(userId, nickName, roles).accessToken()

            jwtProvider.extractUserId(token) shouldBe userId
        }
    }

    describe("extractNickName()") {
        it("토큰에서 nickName을 정확히 추출한다") {
            val token = jwtProvider.generate(userId, nickName, roles).accessToken()

            jwtProvider.extractNickName(token) shouldBe nickName
        }
    }

    describe("extractRoles()") {
        it("토큰에서 roles를 정확히 추출한다") {
            val token = jwtProvider.generate(userId, nickName, roles).accessToken()

            jwtProvider.extractRoles(token) shouldContain "USER"
        }

        it("여러 roles도 모두 추출한다") {
            val multiRoles = setOf("USER", "ADMIN")
            val token = jwtProvider.generate(userId, nickName, multiRoles).accessToken()
            val extracted = jwtProvider.extractRoles(token)

            extracted shouldContain "USER"
            extracted shouldContain "ADMIN"
        }
    }
})
