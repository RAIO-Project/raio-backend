package raio.donation.rdb.repository;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import raio.donation.rdb.entity.DonationsEntity;

import java.time.Instant;
import java.util.stream.Stream;

public interface DonationsJpaRepository extends JpaRepository<DonationsEntity, Long> {

    /**
     * 스트리머가 받은 정상 후원을 기간으로 커서 조회 (최신순).
     *
     * <p>{@code List} 가 아니라 {@code Stream} 을 반환한다. Hibernate 가 ScrollableResults 로
     * 결과를 조금씩 읽어오므로, 결과가 수만 건이어도 전체를 메모리에 올리지 않는다.
     *
     * <p>
     * <ul>
     *   <li>반드시 트랜잭션 안에서 호출해야 한다. 순회가 끝날 때까지 커넥션이 열려 있어야 한다.</li>
     *   <li>반환된 Stream 은 반드시 닫아야 한다. 닫지 않으면 DB 커서가 반납되지 않는다.</li>
     * </ul>
     *
     * <p>{@code HINT_FETCH_SIZE} 를 지정하는 이유는, 이 값이 없으면 JDBC 드라이버가 결과를 한 번에
     * 모두 가져오는 경우가 있어 커서를 쓰는 의미가 사라지기 때문이다. 정상 건만 조회.
     */
    @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "500"))
    @Query("""
            SELECT d
              FROM DonationsEntity d
             WHERE d.receiverId = :receiverId
               AND d.isBlocked  = false
               AND d.isRefunded = false
               AND d.createdAt >= :periodStartAt
               AND d.createdAt <= :periodEndAt
             ORDER BY d.createdAt DESC
            """)
    Stream<DonationsEntity> streamReceivedDonations(
            @Param("receiverId") Long receiverId,
            @Param("periodStartAt") Instant periodStartAt,
            @Param("periodEndAt") Instant periodEndAt
    );
}
