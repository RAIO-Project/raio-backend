package raio.settlement.adapter.batch.chunk;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.data.domain.PageRequest;
import raio.settlement.application.usecase.SettlementSettingReadUseCase;
import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;

import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;

/**
 * 정산 실행 대상(active=true && nextSettlementAt <= now)인 정산 설정을 조회하는 리더.
 *
 * <p>이 리더는 항상 0페이지만 다시 조회한다. 각 청크가 처리한 스트리머는 writer 단계에서
 * nextSettlementAt이 전진되어 조회 조건에서 자연히 빠지므로, 오프셋을 증가시키며 페이징하면
 * 아직 처리되지 않은 대상을 건너뛰게 된다 - 매번 0페이지를 다시 읽는 것이 남은 대상 전체다.</p>
 *
 * <p>이 리더 인스턴스는 스케줄러에 의해 매 실행마다 재사용되는 싱글턴이므로,
 * {@link #open(ExecutionContext)}에서 매 스텝 실행 시작 시점마다 기준 시각(now)을 새로 캡처한다.</p>
 */
@RequiredArgsConstructor
public class SettlementDueSettingsItemReader extends ItemStreamSupport implements ItemReader<SettlementSettingSummary> {

    private final SettlementSettingReadUseCase settlementSettingReadUseCase;
    private final int pageSize;

    private Instant executionAt;
    private Iterator<SettlementSettingSummary> currentPage;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionAt = Instant.now();
        this.currentPage = Collections.emptyIterator();
    }

    @Override
    public SettlementSettingSummary read() {
        if (!currentPage.hasNext()) {
            fetchNextPage();
        }

        return currentPage.hasNext() ? currentPage.next() : null;
    }

    private void fetchNextPage() {
        var page = settlementSettingReadUseCase.getSettlementDueSettings(executionAt, PageRequest.of(0, pageSize));

        currentPage = page.getContent().iterator();
    }
}
