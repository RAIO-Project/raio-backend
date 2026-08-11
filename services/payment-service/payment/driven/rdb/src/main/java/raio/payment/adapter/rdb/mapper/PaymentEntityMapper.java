package raio.payment.adapter.rdb.mapper;

import org.mapstruct.Mapper;
import raio.payment.readmodel.PaymentReadModels.PaymentDetail;
import raio.payment.readmodel.PaymentReadModels.PaymentSummary;
import raio.payment.domain.Payment;
import raio.payment.adapter.rdb.entity.PaymentEntity;

@Mapper(componentModel = "spring")
public interface PaymentEntityMapper {

    Payment toDomain(PaymentEntity entity);

    PaymentEntity toEntity(Payment payment);

    PaymentDetail toDetail(PaymentEntity entity);

    PaymentSummary toSummary(PaymentEntity entity);
}
