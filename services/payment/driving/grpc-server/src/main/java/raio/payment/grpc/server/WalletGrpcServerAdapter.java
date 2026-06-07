package raio.payment.grpc.server;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import raio.payment.application.usecase.WalletCreateUseCase;
import raio.payment.grpc.CreateWalletRequest;
import raio.payment.grpc.CreateWalletResponse;
import raio.payment.grpc.WalletCommandServiceGrpc;

@GrpcService
@RequiredArgsConstructor
public class WalletGrpcServerAdapter
        extends WalletCommandServiceGrpc.WalletCommandServiceImplBase {
    
    private final WalletCreateUseCase walletCreateUseCase;
    
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
}