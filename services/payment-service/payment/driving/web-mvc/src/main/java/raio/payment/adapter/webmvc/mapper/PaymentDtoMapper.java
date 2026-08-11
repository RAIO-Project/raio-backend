package raio.payment.adapter.webmvc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import raio.payment.application.command.PaymentCommands.ConfirmCommand;
import raio.payment.application.command.PaymentCommands.PrepareCommand;
import raio.payment.adapter.webmvc.dto.PaymentCommandDto.PaymentConfirmRequest;
import raio.payment.adapter.webmvc.dto.PaymentCommandDto.PaymentPrepareRequest;

@Mapper(componentModel = "spring")
public interface PaymentDtoMapper {
   
   PrepareCommand toPrepareCommand(PaymentPrepareRequest request);
   
   @Mapping(target = "externalKey", source = "paymentKey")
   ConfirmCommand toConfirmCommand(PaymentConfirmRequest request);
}