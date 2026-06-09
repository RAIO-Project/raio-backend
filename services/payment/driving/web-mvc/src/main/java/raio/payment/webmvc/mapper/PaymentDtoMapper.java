package raio.payment.webmvc.mapper;

import org.mapstruct.Mapper;
import raio.payment.application.command.PaymentCommands.ConfirmCommand;
import raio.payment.application.command.PaymentCommands.PrepareCommand;
import raio.payment.webmvc.dto.PaymentCommandDto.PaymentConfirmRequest;
import raio.payment.webmvc.dto.PaymentCommandDto.PaymentPrepareRequest;

@Mapper(componentModel = "spring")
public interface PaymentDtoMapper {
   
   PrepareCommand toPrepareCommand(PaymentPrepareRequest request);
   
   ConfirmCommand toConfirmCommand(PaymentConfirmRequest request);
}