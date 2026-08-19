package raio.settlement.adapter.grpc.server;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import raio.settlement.application.usecase.SettlementSettingCreateUseCase;
import raio.settlement.grpc.CreateSettlementSettingRequest;
import raio.settlement.grpc.CreateSettlementSettingResponse;
import raio.settlement.grpc.SettlementCommandServiceGrpc;

@GrpcService
@RequiredArgsConstructor
public class SettlementGrpcServerAdapter
        extends SettlementCommandServiceGrpc.SettlementCommandServiceImplBase {
    
    private final SettlementSettingCreateUseCase settlementSettingCreateUseCase;
    
    @Override
    public void createSettlementSetting(CreateSettlementSettingRequest request,
                                        StreamObserver<CreateSettlementSettingResponse> responseObserver) {
        var settlementSetting = settlementSettingCreateUseCase.createSettlementSetting(request.getStreamerId());
        
        var response = CreateSettlementSettingResponse.newBuilder()
                .setStreamerId(settlementSetting.getStreamerId())
                .setCurrentCycle(settlementSetting.getCurrentCycle().toString())
                .setActiveYn(settlementSetting.isActive() ? "Y" : "N")
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
