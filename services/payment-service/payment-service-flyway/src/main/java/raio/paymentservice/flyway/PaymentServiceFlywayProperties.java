package raio.paymentservice.flyway;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Payment Service의 Flyway 설정을 관리한다.
 *
 * <p>Payment, Wallet, Settlement 도메인이 공유하는
 * 데이터베이스 스키마의 마이그레이션 설정을 정의한다.</p>
 */
@ConfigurationProperties(prefix = "payment-service.flyway")
public record PaymentServiceFlywayProperties(
        String locations,
        String schema,
        boolean createSchemas,
        boolean baselineOnMigrate,
        boolean validateOnMigrate,
        boolean outOfOrder,
        String historyTable
) {
}