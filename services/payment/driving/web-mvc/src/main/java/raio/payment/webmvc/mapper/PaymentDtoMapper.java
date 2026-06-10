package raio.payment.webmvc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import raio.payment.application.command.PaymentCommands.ConfirmCommand;
import raio.payment.application.command.PaymentCommands.PrepareCommand;
import raio.payment.webmvc.dto.PaymentCommandDto.PaymentConfirmRequest;
import raio.payment.webmvc.dto.PaymentCommandDto.PaymentPrepareRequest;

@Mapper(componentModel = "spring")
public interface PaymentDtoMapper {
   
   PrepareCommand toPrepareCommand(PaymentPrepareRequest request);
   
   @Mapping(target = "externalKey", source = "paymentKey")
   ConfirmCommand toConfirmCommand(PaymentConfirmRequest request);
}