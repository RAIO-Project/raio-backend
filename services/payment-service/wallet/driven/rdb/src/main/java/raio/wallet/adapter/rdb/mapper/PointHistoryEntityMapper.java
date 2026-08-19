package raio.wallet.adapter.rdb.mapper;

import org.mapstruct.Mapper;
import raio.wallet.readmodel.PointHistoryReadModels.PointHistoryDetail;
import raio.wallet.readmodel.PointHistoryReadModels.PointHistorySummary;
import raio.wallet.domain.PointHistory;
import raio.wallet.adapter.rdb.entity.PointHistoryEntity;

@Mapper(componentModel = "spring")
public interface PointHistoryEntityMapper {
    
    PointHistory toDomain(PointHistoryEntity entity);
    
    PointHistoryEntity toEntity(PointHistory history);
    
    PointHistoryDetail toDetail(PointHistoryEntity entity);
    
    PointHistorySummary toSummary(PointHistoryEntity entity);
}
