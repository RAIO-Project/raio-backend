package raio.payment.rdb.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import raio.payment.domain.type.PgProvider;

import java.util.Arrays;

import static raio.payment.exception.PaymentErrorCode.PAYMENT_INVALID_PG_PROVIDER;

@Getter
@RequiredArgsConstructor
public enum PgProviderEntityType {

    TOSS((short) 1),
    NAVER((short) 2),
    KAKAO((short) 3),
    INICIS((short) 4);

    private final short code;

    public static PgProviderEntityType valueOf(PgProvider provider) {
        return switch (provider) {
            case TOSS -> TOSS;
            case NAVER -> NAVER;
            case KAKAO -> KAKAO;
            case INICIS -> INICIS;
        };
    }

    public static PgProviderEntityType fromCode(short code) {
        return Arrays.stream(values())
                .filter(t -> t.code == code)
                .findFirst()
                .orElseThrow(PAYMENT_INVALID_PG_PROVIDER::exception);
    }

    public PgProvider toDomain() {
        return switch (this) {
            case TOSS -> PgProvider.TOSS;
            case NAVER -> PgProvider.NAVER;
            case KAKAO -> PgProvider.KAKAO;
            case INICIS -> PgProvider.INICIS;
        };
    }
}
