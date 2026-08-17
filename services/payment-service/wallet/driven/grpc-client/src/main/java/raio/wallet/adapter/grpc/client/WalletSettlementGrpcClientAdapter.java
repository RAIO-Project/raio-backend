package raio.wallet.adapter.grpc.client;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import raio.settlement.domain.SettlementSetting;
import raio.settlement.domain.type.SettlementCycle;
import raio.settlement.grpc.CreateSettlementSettingRequest;
import raio.settlement.grpc.SettlementCommandServiceGrpc.SettlementCommandServiceBlockingStub;
import raio.wallet.application.port.SettlementCommandPort;

import java.time.Instant;

import static raio.wallet.exception.WalletErrorCode.SETTLEMENT_SERVICE_TIMEOUT;
import static raio.wallet.exception.WalletErrorCode.SETTLEMENT_SERVICE_UNAVAILABLE;

@Slf4j
@Component
public class WalletSettlementGrpcClientAdapter implements SettlementCommandPort {

    private final SettlementCommandServiceBlockingStub settlementStub;

    public WalletSettlementGrpcClientAdapter(
            @Qualifier("walletSettlementCommandServiceBlockingStub")
            final SettlementCommandServiceBlockingStub settlementStub) {
        this.settlementStub = settlementStub;
    }

    @Override
    public SettlementSetting save(String streamerId) {
        try {
            var settlementSetting = settlementStub.createSettlementSetting(
                    CreateSettlementSettingRequest.newBuilder()
                            .setStreamerId(streamerId)
                            .build());

            log.info("Settlement setting created streamerId: {}, cycle: {}",
                    settlementSetting.getStreamerId(),
                    settlementSetting.getCurrentCycle());

            return SettlementSetting.create(
                    streamerId,
                    SettlementCycle.valueOf(settlementSetting.getCurrentCycle()),
                    Instant.now());
        } catch (StatusRuntimeException e) {
            throw switch (e.getStatus().getCode()) {
                case UNAVAILABLE -> SETTLEMENT_SERVICE_UNAVAILABLE.exception(e);

                case DEADLINE_EXCEEDED -> SETTLEMENT_SERVICE_TIMEOUT.exception(e);

                default -> throw e;
            };
        }
    }
}
