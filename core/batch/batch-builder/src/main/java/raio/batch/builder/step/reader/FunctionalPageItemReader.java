package raio.batch.builder.step.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.data.domain.Page;

/**
 * Pageable 조회 함수를 Spring Batch ItemReader로 변환한다.
 * Page 단위로 데이터를 조회하고 read() 호출마다 Item 하나를 반환한다.
 */
public final class FunctionalPageItemReader<T> implements ItemReader<T> {
    
    private final int pageSize;
    private final Function<Pageable, Page<T>> pageFetcher;
    
    private int pageNumber;
    private Iterator<T> iterator = Collections.emptyIterator();
    
    public FunctionalPageItemReader(
            int pageSize,
            Function<Pageable, Page<T>> pageFetcher
    ) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException(
                    "pageSize must be greater than 0"
            );
        }
        
        this.pageSize = pageSize;
        this.pageFetcher = Objects.requireNonNull(
                pageFetcher,
                "pageFetcher"
        );
    }
    
    @Override
    public T read() {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        
        var page = pageFetcher.apply(
                PageRequest.of(pageNumber++, pageSize)
        );
        
        if (page.isEmpty()) {
            return null;
        }
        
        iterator = page.iterator();
        
        return iterator.next();
    }
}