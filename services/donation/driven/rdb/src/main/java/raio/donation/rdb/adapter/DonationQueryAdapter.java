package raio.donation.rdb.adapter;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import raio.donation.DonationReadModels.ReceivedDonation;
import raio.donation.application.port.DonationQueryPort;
import raio.donation.rdb.entity.DonationsEntity;
import raio.donation.rdb.repository.DonationsJpaRepository;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 후원 조회 어댑터 (커서 기반).
 *
 * <p>결과를 리스트로 모으지 않고 DB 커서로 읽으면서 즉시 소비자에게 넘긴다.
 * <ol>
 *   <li><b>트랜잭션</b> — 커서는 순회가 끝날 때까지 커넥션을 잡고 있어야 하므로
 *       {@code @Transactional(readOnly = true)} 로 메서드 전체를 감싼다.
 *       이게 없으면 스트리밍 쿼리 실행 시점에 바로 예외가 난다.</li>
 *   <li><b>Stream 닫기</b> — try-with-resources 로 확실히 닫는다. 닫지 않으면 DB 커서가
 *       반납되지 않아 결국 커서가 고갈된다.</li>
 *   <li><b>detach</b> — 커서로 읽어도 Hibernate 는 읽은 엔티티를 영속성 컨텍스트에 계속 쌓는다.
 *       그대로 두면 수만 건을 지나며 힙이 차올라 커서를 쓴 의미가 사라지므로, 변환 직후
 *       영속성 컨텍스트에서 떼어낸다.</li>
 * </ol>
 */
@Repository
@RequiredArgsConstructor
public class DonationQueryAdapter implements DonationQueryPort {

    private final DonationsJpaRepository donationsJpaRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public void forEachReceivedDonation(
            Long streamerId,
            Instant periodStartAt,
            Instant periodEndAt,
            Consumer<ReceivedDonation> consumer
    ) {
        try (Stream<DonationsEntity> cursor =
                     donationsJpaRepository.streamReceivedDonations(streamerId, periodStartAt, periodEndAt)) {

            cursor.forEach(entity -> {
                consumer.accept(toReadModel(entity));

                // 읽은 엔티티를 영속성 컨텍스트에서 제거해 메모리 사용량을 일정하게 유지한다.
                entityManager.detach(entity);
            });
        }
    }

    private ReceivedDonation toReadModel(DonationsEntity entity) {
        return ReceivedDonation.builder()
                .donationId(entity.getId())
                .streamId(entity.getStreamId())
                .senderId(entity.getSenderId())
                .amount(entity.getAmount())
                .message(entity.getMessage())
                .donatedAt(entity.getCreatedAt())
                .build();
    }
}
