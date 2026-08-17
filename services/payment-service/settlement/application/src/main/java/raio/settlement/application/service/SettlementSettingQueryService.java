package raio.settlement.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import raio.settlement.application.port.SettlementSettingQueryRepositoryPort;
import raio.settlement.application.usecase.SettlementSettingReadUseCase;
import raio.settlement.readmodel.SettlementReadModels.SettlementSettingSummary;

import java.time.Instant;

import static raio.settlement.exception.SettlementErrorCode.SETTLEMENT_SETTING_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SettlementSettingQueryService implements SettlementSettingReadUseCase {

    private final SettlementSettingQueryRepositoryPort settlementSettingQueryRepositoryPort;
    
    /**
     * 스트리머의 정산 설정을 조회한다.
     *
     * @param streamerId 스트리머 식별자
     * @return 스트리머의 정산 설정
     */
    @Override
    public SettlementSettingSummary getSettlementSetting(String streamerId) {
        return settlementSettingQueryRepositoryPort.findSettlementSettingByStreamerId(streamerId)
                .orElseThrow(SETTLEMENT_SETTING_NOT_FOUND::exception);
    }
    
    /**
     * 현재 시점을 기준으로 정산 실행이 필요한 설정을 조회한다.
     * 1. 정산 설정이 활성화되어 있어야 한다.
     * 2. 다음 정산 예정 시각(nextSettlementAt)이 현재 시각 이하이어야 한다.
     *
     * @param now      정산 실행 여부를 판단할 기준 시각
     * @param pageable 페이지 조회 조건
     * @return 현재 정산 실행 대상인 정산 설정 목록
     */
    @Override
    public Page<SettlementSettingSummary> getSettlementDueSettings(Instant now, Pageable pageable) {
        return settlementSettingQueryRepositoryPort.findSettlementDueSettings(true, now, pageable);
    }
}
