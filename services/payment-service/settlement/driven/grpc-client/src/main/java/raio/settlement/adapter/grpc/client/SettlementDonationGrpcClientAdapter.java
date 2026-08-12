package raio.settlement.adapter.grpc.client;

import org.springframework.stereotype.Component;
import raio.settlement.application.port.SettlementDonationQueryPort;

import java.time.Instant;
import java.util.List;

@Component
public class SettlementDonationGrpcClientAdapter implements SettlementDonationQueryPort {
    
    // TO-DO: Donation 모듈에서 streamerId와 정산 기간 날짜 기준으로 내역을 조회해온다
    
    @Override
    public List<SettlementDonationRevenue> findUnsettledDonations(String streamerId, Instant periodStartAt, Instant periodEndAt) {
        return List.of();
    }
}
