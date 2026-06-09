package raio.payment.webmvc;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raio.payment.application.usecase.PaymentConfirmUseCase;
import raio.payment.application.usecase.PaymentPrepareUseCase;
import raio.payment.webmvc.dto.PaymentCommandDto.PaymentConfirmRequest;
import raio.payment.webmvc.dto.PaymentCommandDto.PaymentPrepareRequest;
import raio.payment.webmvc.dto.PaymentCommandDto.ConfirmPaymentResponse;
import raio.payment.webmvc.dto.PaymentCommandDto.PreparePaymentResponse;
import raio.payment.webmvc.mapper.PaymentDtoMapper;

@Tag(name = "Payment", description = "결제 API")
@RestController
@RequestMapping("/payment/payments")
@RequiredArgsConstructor
public class PaymentCommandApi {

    private final PaymentPrepareUseCase paymentPrepareUseCase;
    private final PaymentConfirmUseCase paymentConfirmUseCase;
    private final PaymentDtoMapper mapper;

    @PostMapping("/prepare")
    public PreparePaymentResponse prepare(@Valid @RequestBody PaymentPrepareRequest command) {
        var payment = paymentPrepareUseCase.prepare(mapper.toPrepareCommand(command));
        
        return PreparePaymentResponse.builder()
                .paymentId(payment.getId())
                .amount(payment.getAmount())
                .orderId(payment.getOrderId())
                .build();
    }

    @PostMapping("/confirm")
    public ConfirmPaymentResponse confirm(@Valid @RequestBody PaymentConfirmRequest command) {
        var payment = paymentConfirmUseCase.confirm(mapper.toConfirmCommand(command));
        
        return ConfirmPaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .status(payment.getStatus().name())
                .build();
    }
}
