package raio.payment.application.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import raio.payment.PaymentReadModels.PaymentDetail;
import raio.payment.PaymentReadModels.PaymentSummary;

import java.util.Optional;

public interface PaymentQueryRepositoryPort {

    Optional<PaymentDetail> findPaymentDetailById(String id);

    Optional<PaymentDetail> findPaymentDetailByOrderId(String orderId);

    Page<PaymentSummary> findPaymentSummaryByUserId(String userId, Pageable pageable);
}
