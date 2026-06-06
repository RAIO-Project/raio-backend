package raio.payment.rdb.mapper;

import org.mapstruct.Mapper;
import raio.payment.domain.Wallet;
import raio.payment.rdb.entity.WalletEntity;

@Mapper(componentModel = "spring")
public interface WalletEntityMapper {

    Wallet toDomain(WalletEntity entity);
    
    WalletEntity toEntity(Wallet wallet);
}
