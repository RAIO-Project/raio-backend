package raio.user.web.response;

import raio.jwt.TokenPair;

public record TokenPairResponse(
        String accessToken,
        String refreshToken
) {
    public static TokenPairResponse from(TokenPair tokenPair) {
        return new TokenPairResponse(tokenPair.accessToken(), tokenPair.refreshToken());
    }
}
