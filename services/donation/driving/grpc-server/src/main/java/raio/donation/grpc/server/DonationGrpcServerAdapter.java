package raio.donation.grpc.server;

import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import raio.donation.DonationReadModels;
import raio.donation.application.usecase.DonationReceivedReadUseCase;
import raio.donation.grpc.DonationQueryServiceGrpc;
import raio.donation.grpc.GetReceivedDonationsRequest;
import raio.donation.grpc.ReceivedDonation;

import java.time.Instant;

/**
 * 스트리머 후원 수령 내역 조회 gRPC 서버 (서버 스트리밍).
 *
 * <p>조회 결과를 모아 한 번에 보내지 않고, DB 커서에서 읽히는 대로 {@code onNext} 로 흘려보낸다.
 * 배치가 기간 전체를 훑어도 단일 응답 크기 제한에 걸리지 않고, 서버 메모리도 건수와 무관하게 일정하다.
 *
 * <p>proto 는 문자열 ID 와 epoch milli 를 쓴다. 서비스 간 경계에서는 타임존 해석 여지가 없는
 * epoch milli 가 안전하고, ID 도 숫자형보다 문자열이 스키마 변경에 유연하다.
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class DonationGrpcServerAdapter
        extends DonationQueryServiceGrpc.DonationQueryServiceImplBase {

    private final DonationReceivedReadUseCase donationReceivedReadUseCase;

    @Override
    public void getReceivedDonations(
            GetReceivedDonationsRequest request,
            StreamObserver<ReceivedDonation> responseObserver
    ) {
        var serverObserver = (ServerCallStreamObserver<ReceivedDonation>) responseObserver;

        try {
            donationReceivedReadUseCase.forEachReceivedDonation(
                    Long.parseLong(request.getStreamerId()),
                    Instant.ofEpochMilli(request.getPeriodStartAt()),
                    Instant.ofEpochMilli(request.getPeriodEndAt()),
                    donation -> emit(serverObserver, donation)
            );

            serverObserver.onCompleted();

        } catch (StreamCancelledException e) {
            // 클라이언트가 끊은 경우. 이미 종료된 스트림이라 onError 를 보내지 않는다.
            log.info("[후원 조회] 클라이언트 취소 - receiverId={}", request.getStreamerId());

        } catch (RuntimeException e) {
            log.error("[후원 조회] 스트리밍 실패 - receiverId={}", request.getStreamerId(), e);
            serverObserver.onError(Status.INTERNAL
                    .withDescription("후원 내역 조회에 실패했습니다.")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    /**
     * 한 건 전송.
     *
     * <p>클라이언트가 이미 끊었다면 더 읽어봐야 버려질 뿐이므로 예외를 던져 커서 순회를 중단시킨다.
     * 이 예외는 위에서 잡아 정상 취소로 처리한다 — 배치가 도중에 죽었을 때 서버가 남은 수만 건을
     * 계속 읽으며 커넥션을 붙잡고 있지 않도록 하기 위한 것이다.
     */
    private void emit(
            ServerCallStreamObserver<ReceivedDonation> observer,
            DonationReadModels.ReceivedDonation donation
    ) {
        if (observer.isCancelled()) {
            throw new StreamCancelledException();
        }

        observer.onNext(ReceivedDonation.newBuilder()
                .setDonationId(String.valueOf(donation.donationId()))
                .setStreamId(String.valueOf(donation.streamId()))
                .setSenderId(String.valueOf(donation.senderId()))
                .setAmount(donation.amount())
                // proto3 는 null 을 허용하지 않는다. 메시지 없는 후원은 빈 문자열로 보낸다.
                .setMessage(donation.message() == null ? "" : donation.message())
                .setDonatedAt(donation.donatedAt().toEpochMilli())
                .build());
    }

    /** 클라이언트 취소로 커서 순회를 중단시키기 위한 내부 신호. */
    private static class StreamCancelledException extends RuntimeException {
        StreamCancelledException() {
            // 제어 흐름용이므로 스택트레이스를 채우지 않는다.
            super(null, null, false, false);
        }
    }
}
