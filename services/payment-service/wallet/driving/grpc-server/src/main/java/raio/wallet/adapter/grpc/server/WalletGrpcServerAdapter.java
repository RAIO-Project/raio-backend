package raio.wallet.adapter.grpc.server;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import raio.wallet.application.usecase.PointChargeUseCase;
import raio.wallet.application.usecase.PointDonateUseCase;
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
import raio.wallet.grpc.WalletCommandServiceGrpc;

@GrpcService
@RequiredArgsConstructor
public class WalletGrpcServerAdapter
        extends WalletCommandServiceGrpc.WalletCommandServiceImplBase {
    
    private final WalletCreateUseCase walletCreateUseCase;
    private final WalletReadUseCase walletReadUseCase;
    private final PointDonateUseCase pointDonateUseCase;
    private final PointChargeUseCase pointChargeUseCase;
    
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
        var wallet = pointDonateUseCase.donate(request.getWalletId(), request.getAmount());
        
        var response = DonatePointResponse.newBuilder()
                .setWalletId(wallet.getId())
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
        var wallet = pointChargeUseCase.charge(request.getWalletId(), request.getAmount());
        
        var response = ChargePointsResponse.newBuilder()
                .setWalletId(wallet.getId())
                .setBalance(wallet.getBalance())
                .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}