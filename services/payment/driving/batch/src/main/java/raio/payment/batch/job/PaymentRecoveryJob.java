package raio.payment.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.batch.builder.job.BatchJobs;
import raio.payment.application.usecase.PaymentRecoveryUseCase;

@Configuration
@RequiredArgsConstructor
public class PaymentRecoveryJob {
    
    private final PaymentRecoveryUseCase paymentRecoveryUseCase;
    
    @Bean
    public Job paymentRecoveryJob(BatchJobs batch) {
        return batch.job("paymentRecoveryJob")
                .taskletStep("paymentRecoveryStep", () -> {
                    paymentRecoveryUseCase.recover();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}