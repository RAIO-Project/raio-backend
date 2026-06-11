package raio.payment.rdb.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import raio.payment.PaymentReadModels.PaymentDetail;
import raio.payment.PaymentReadModels.PaymentSummary;
import raio.payment.application.port.PaymentQueryRepositoryPort;
import raio.payment.rdb.mapper.PaymentEntityMapper;
import raio.payment.rdb.repository.PaymentJpaRepository;

import java.util.Optional;

import static raio.payment.rdb.entity.QPaymentEntity.paymentEntity;

@Repository
@RequiredArgsConstructor
public class PaymentQueryAdapter implements PaymentQueryRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentEntityMapper paymentEntityMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<PaymentDetail> findPaymentDetailById(String id) {
        return paymentJpaRepository.findById(Long.parseLong(id))
                .map(paymentEntityMapper::toDetail);
    }

    @Override
    public Optional<PaymentDetail> findPaymentDetailByOrderId(String orderId) {
        return paymentJpaRepository.findByOrderId(orderId)
                .map(paymentEntityMapper::toDetail);
    }

    @Override
    public Page<PaymentSummary> findPaymentSummaryByUserId(String userId, Pageable pageable) {
        var content = queryFactory.select(paymentEntity)
                .from(paymentEntity)
                .where(paymentEntity.userId.eq(Long.parseLong(userId)))
                .orderBy(paymentEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(paymentEntityMapper::toSummary)
                .toList();

        var countQuery = queryFactory.select(paymentEntity.count())
                .from(paymentEntity)
                .where(paymentEntity.userId.eq(Long.parseLong(userId)));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
