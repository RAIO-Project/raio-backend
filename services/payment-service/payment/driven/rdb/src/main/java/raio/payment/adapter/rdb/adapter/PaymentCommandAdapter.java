package raio.payment.adapter.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import raio.payment.application.port.PaymentCommandRepositoryPort;
import raio.payment.domain.Payment;
import raio.payment.domain.type.PaymentStatus;
import raio.payment.adapter.rdb.entity.type.PaymentStatusEntityType;
import raio.payment.adapter.rdb.mapper.PaymentEntityMapper;
import raio.payment.adapter.rdb.repository.PaymentJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Repository
@RequiredArgsConstructor
public class PaymentCommandAdapter implements PaymentCommandRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentEntityMapper paymentEntityMapper;
    
    @Override
    @Transactional
    public <T> T transaction(Supplier<T> supplier) {
        return supplier.get();
    }
    
    @Override
    public Payment save(Payment payment) {
        var saved = paymentJpaRepository.saveAndFlush(paymentEntityMapper.toEntity(payment));
        return paymentEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findByIdForUpdate(String id) {
        return paymentJpaRepository.findByIdForUpdate(Long.parseLong(id))
                .map(paymentEntityMapper::toDomain);
    }
    
    @Override
    public List<Payment> findApprovingWithPaymentKey() {
        return paymentJpaRepository
                .findApprovingWithPaymentKey(PaymentStatusEntityType.APPROVING)
                .stream()
                .map(paymentEntityMapper::toDomain)
                .toList();
    }
    
    @Override
    public Optional<Payment> updateStatus(String id, PaymentStatus status, String externalTid, String failReason) {
        Long entityId = Long.parseLong(id);
        
        PaymentStatusEntityType entityStatus = PaymentStatusEntityType.valueOf(status);

        int updated = paymentJpaRepository.updateStatus(entityId, entityStatus, externalTid, failReason);
        
        if (updated == 0) {
            return Optional.empty();
        }

        return paymentJpaRepository.findById(entityId)
                .map(paymentEntityMapper::toDomain);
    }
}
