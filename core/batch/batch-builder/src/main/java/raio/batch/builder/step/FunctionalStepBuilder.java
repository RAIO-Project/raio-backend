package raio.batch.builder.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;
import raio.batch.builder.tasklet.SimpleTasklet;

@RequiredArgsConstructor
public class FunctionalStepBuilder {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    public Step tasklet(String stepName, SimpleTasklet tasklet) {
        return new StepBuilder(stepName, jobRepository)
                .tasklet((contribution, chunkContext) -> tasklet.execute(), transactionManager)
                .build();
    }
    
    public <I, O> Step chunk(
            String stepName,
            int chunkSize,
            ChunkStepSpec<I, O> spec
    ) {
        var builder = new StepBuilder(stepName, jobRepository)
                .<I, O>chunk(chunkSize, transactionManager);
        
        return spec.apply(builder).build();
    }
}