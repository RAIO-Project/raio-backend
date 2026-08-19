package raio.donation.rdb.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import raio.donation.domain.Donations;
import raio.donation.rdb.entity.DonationsEntity;

@Mapper(componentModel = "spring")
public interface DonationsEntityMapper {

    Donations toDomain(DonationsEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DonationsEntity toEntity(Donations donations);
}
