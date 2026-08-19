package raio.wallet.adapter.grpc.server;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import raio.wallet.application.usecase.PointChargeUseCase;
import raio.wallet.application.usecase.PointDonateUseCase;
import raio.wallet.application.usecase.PointRefundUseCase;
import raio.wallet.application.usecase.WalletCreateUseCase;
import raio.wallet.application.usecase.WalletReadUseCase;
import raio.wallet.grpc.ChargePointsRequest;
import raio.wallet.grpc.ChargePointsResponse;
import raio.wallet.grpc.CreateWalletRequest;
import raio.wallet.grpc.CreateWalletResponse;
import raio.wallet.grpc.DonatePointRequest;
import raio.wallet.grpc.DonatePointResponse;
import raio.wallet.grpc.GetWalletRequest;
import raio.wallet.grpc.GetWalletResponse;
import raio.wallet.grpc.RefundPointsRequest;
import raio.wallet.grpc.RefundPointsResponse;
import raio.wallet.grpc.WalletCommandServiceGrpc;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class WalletGrpcServerAdapter
        extends WalletCommandServiceGrpc.WalletCommandServiceImplBase {
    
    private final WalletCreateUseCase walletCreateUseCase;
    private final WalletReadUseCase walletReadUseCase;
    private final PointDonateUseCase pointDonateUseCase;
    private final PointChargeUseCase pointChargeUseCase;
    private final PointRefundUseCase pointRefundUseCase;
    
    @Override
    public void createWallet(
            CreateWalletRequest request,
            StreamObserver<CreateWalletResponse> responseObserver
    ) {
        var wallet = walletCreateUseCase.create(request.getUserId());
        
        var response = CreateWalletResponse.newBuilder()
                .setWalletId(wallet.getId())
                .setUserId(wallet.getUserId())
                .setBalance(wallet.getBalance())
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    @Override
    public void donatePoint(
            DonatePointRequest request,
            StreamObserver<DonatePointResponse> responseObserver
    ) {
        var wallet = pointDonateUseCase.donate(request.getUserId(), request.getAmount(), request.getSourceId());
        
        var response = DonatePointResponse.newBuilder()
                .setUserId(wallet.getUserId())
                .setBalance(wallet.getBalance())
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    @Override
    public void getWallet(GetWalletRequest request,
                          StreamObserver<GetWalletResponse> responseObserver
    ) {
        var wallet = walletReadUseCase.getWallet(request.getUserId());
        
        var response = GetWalletResponse.newBuilder()
                .setWalletId(wallet.getId())
                .setUserId(wallet.getUserId())
                .setBalance(wallet.getBalance())
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    @Override
    public void chargePoints(ChargePointsRequest request,
                             StreamObserver<ChargePointsResponse> responseObserver
    ) {
        var wallet = pointChargeUseCase.charge(request.getWalletId(), request.getSourceId(), request.getAmount());
        
        var response = ChargePointsResponse.newBuilder()
                .setWalletId(wallet.getId())
                .setBalance(wallet.getBalance())
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    @Override
    public void refundPoints(RefundPointsRequest request,
                             StreamObserver<RefundPointsResponse> responseObserver
    ) {
        var wallet = walletReadUseCase.getWallet(request.getUserId());
        
        if(wallet == null) {
            log.error("[지갑 조회 실패]: userId={}", request.getUserId());
            
        } else {
            pointRefundUseCase.refund(wallet.getId(), request.getSourceId(), request.getAmount());
            
            var response = RefundPointsResponse.newBuilder()
                    .setUserId(wallet.getUserId())
                    .setBalance(wallet.getBalance())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}