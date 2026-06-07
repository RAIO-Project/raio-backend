package raio.payment.rdb.mapper;

import org.mapstruct.Mapper;
import raio.payment.PointHistoryReadModels.PointHistoryDetail;
import raio.payment.PointHistoryReadModels.PointHistorySummary;
import raio.payment.domain.PointHistory;
import raio.payment.rdb.entity.PointHistoryEntity;

@Mapper(componentModel = "spring")
public interface PointHistoryEntityMapper {
    
    PointHistory toDomain(PointHistoryEntity entity);
    
    PointHistoryEntity toEntity(PointHistory history);
    
    PointHistoryDetail toDetail(PointHistoryEntity entity);
    
    PointHistorySummary toSummary(PointHistoryEntity entity);
}
