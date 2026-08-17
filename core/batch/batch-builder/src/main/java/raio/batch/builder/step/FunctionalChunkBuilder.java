package raio.batch.builder.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import raio.batch.builder.step.reader.FunctionalPageItemReader;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Spring Batch의 {@link SimpleStepBuilder}를 함수형 API로 추상화한 Chunk Step Builder.
 *
 * 도메인 배치에서는 Spring Batch의 Reader/Processor/Writer 구현체를
 * 직접 작성하지 않고 함수 중심으로 Step을 구성할 수 있도록 한다.
 *
 * @param <I> Reader 입력 타입
 * @param <O> Processor 출력 및 Writer 입력 타입
 */
public final class FunctionalChunkBuilder<I, O> {
    
    private final SimpleStepBuilder<I, O> builder;
    
    public FunctionalChunkBuilder(SimpleStepBuilder<I, O> builder) {
        this.builder = builder;
    }
    
    /**
     * 기존 Spring Batch ItemReader를 직접 등록한다.
     */
    public FunctionalChunkBuilder<I, O> reader(ItemReader<I> reader) {
        builder.reader(reader);
        return this;
    }
    
    /**
     * Pageable 기반 조회 함수를 Page ItemReader로 변환하여 등록한다.
     */
    public FunctionalChunkBuilder<I, O> pageReader(
            int pageSize,
            Function<Pageable, Page<I>> pageFetcher
    ) {
        builder.reader(
                new FunctionalPageItemReader<>(
                        pageSize,
                        pageFetcher
                )
        );
        
        return this;
    }
    
    /**
     * 입력 데이터를 Writer에서 사용할 출력 데이터로 변환한다.
     */
    public FunctionalChunkBuilder<I, O> processor(
            Function<I, O> processor
    ) {
        builder.processor(processor::apply);
        return this;
    }
    
    /**
     * Chunk의 각 Item에 대해 전달받은 함수를 실행한다.
     *
     * UseCase 메서드 참조를 직접 전달할 수 있다.
     *
     * ex)
     * .writer(settlementCalculateUseCase::calculate)
     */
    public FunctionalChunkBuilder<I, O> writer(
            Consumer<O> writer
    ) {
        builder.writer(chunk -> {
            for (O item : chunk) {
                writer.accept(item);
            }
        });
        
        return this;
    }
    
    Step build() {
        return builder.build();
    }
}