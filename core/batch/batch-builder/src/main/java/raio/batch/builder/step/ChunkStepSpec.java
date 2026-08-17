package raio.batch.builder.step;

@FunctionalInterface
public interface ChunkStepSpec<I, O> {
    
    FunctionalChunkBuilder<I, O> apply(FunctionalChunkBuilder<I, O> builder);
}