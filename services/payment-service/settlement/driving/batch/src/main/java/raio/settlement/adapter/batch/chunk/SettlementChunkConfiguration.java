package raio.settlement.adapter.batch.chunk;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import raio.batch.builder.job.BatchJobs;
import raio.settlement.application.command.SettlementCommands.SettlementCalculateCommand;
import raio.settlement.application.usecase.SettlementCalculateUseCase;
import raio.settlement.application.usecase.SettlementSettingReadUseCase;
import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;

/**
 * 정산 배치는 UseCase를 오케스트레이션(호출)만 할 뿐, 정산 대상 판단이나
 * 정산 기간 계산 같은 비즈니스 결정은 전부 Settlement application/domain 계층이 담당한다.
 */
@Configuration
@RequiredArgsConstructor
public class SettlementChunkConfiguration {

    private static final int CHUNK_SIZE = 50;

    private final SettlementSettingReadUseCase settlementSettingReadUseCase;
    private final SettlementCalculateUseCase settlementCalculateUseCase;

    @Bean
    public Job settlementCalculateJob(BatchJobs batch) {
        return batch.job("settlementCalculateJob")
                // I: 정산 설정 조회 결과 → O: 정산 계산 Command
                .<SettlementSettingSummary, SettlementCalculateCommand>chunkStep(
                        "settlementCalculateStep",
                        CHUNK_SIZE,
                        step -> step
                                // 정산 실행 대상 조회 (매 실행마다 0페이지를 다시 읽는 자기 소모형 리더 - 상세는 클래스 주석 참고)
                                .reader(new SettlementDueSettingsItemReader(settlementSettingReadUseCase, CHUNK_SIZE))
                                // 조회 결과를 정산 Command로 변환
                                .processor(summary -> new SettlementCalculateCommand(
                                        summary.streamerId(),
                                        summary.nextSettlementAt()
                                ))
                                // 정산 UseCase 실행
                                .writer(settlementCalculateUseCase::calculate)
                )
                .build();
    }
}
