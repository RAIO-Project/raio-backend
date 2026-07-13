package raio.payment.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import raio.payment.application.port.ApprovingPaymentQueryPort;
import raio.payment.domain.Payment;
import raio.payment.rdb.entity.type.PaymentStatusEntityType;
import raio.payment.rdb.mapper.PaymentEntityMapper;
import raio.payment.rdb.repository.PaymentJpaRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ApprovingPaymentQueryAdapter implements ApprovingPaymentQueryPort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentEntityMapper paymentEntityMapper;

    @Override
    public List<Payment> findApprovingWithPaymentKey() {
        return paymentJpaRepository
                .findApprovingWithPaymentKey(PaymentStatusEntityType.APPROVING)
                .stream()
                .map(paymentEntityMapper::toDomain)
                .toList();
    }
}