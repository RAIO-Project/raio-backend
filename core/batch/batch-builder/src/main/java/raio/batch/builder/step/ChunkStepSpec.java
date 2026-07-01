package raio.batch.builder.step;

import org.springframework.batch.core.step.builder.SimpleStepBuilder;

@FunctionalInterface
public interface ChunkStepSpec<I, O> {
    
    SimpleStepBuilder<I, O> apply(SimpleStepBuilder<I, O> builder);
}