package raio.batch.builder.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import raio.batch.builder.step.ChunkStepSpec;
import raio.batch.builder.step.FunctionalStepBuilder;
import raio.batch.builder.tasklet.SimpleTasklet;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class FunctionalJobBuilder {
    
    private final JobRepository jobRepository;
    private final FunctionalStepBuilder stepBuilder;
    private final String jobName;
    private final List<Step> steps = new ArrayList<>();
    
    public FunctionalJobBuilder taskletStep(String stepName, SimpleTasklet tasklet) {
        steps.add(stepBuilder.tasklet(stepName, tasklet));
        return this;
    }
    
    public <I, O> FunctionalJobBuilder chunkStep(
            String stepName,
            int chunkSize,
            ChunkStepSpec<I, O> spec
    ) {
        steps.add(stepBuilder.chunk(stepName, chunkSize, spec));
        return this;
    }
    
    public Job build() {
        if (steps.isEmpty()) {
            throw new IllegalStateException("At least one step is required. jobName=" + jobName);
        }
        
        var builder = new JobBuilder(jobName, jobRepository)
                .start(steps.get(0));
        
        for (int i = 1; i < steps.size(); i++) {
            builder.next(steps.get(i));
        }
        
        return builder.build();
    }
}