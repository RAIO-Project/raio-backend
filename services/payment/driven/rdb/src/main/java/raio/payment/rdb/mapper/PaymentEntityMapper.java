package raio.payment.rdb.mapper;

import org.mapstruct.Mapper;
import raio.payment.PaymentReadModels.PaymentDetail;
import raio.payment.PaymentReadModels.PaymentSummary;
import raio.payment.domain.Payment;
import raio.payment.rdb.entity.PaymentEntity;

@Mapper(componentModel = "spring")
public interface PaymentEntityMapper {

    Payment toDomain(PaymentEntity entity);

    PaymentEntity toEntity(Payment payment);

    PaymentDetail toDetail(PaymentEntity entity);

    PaymentSummary toSummary(PaymentEntity entity);
}
