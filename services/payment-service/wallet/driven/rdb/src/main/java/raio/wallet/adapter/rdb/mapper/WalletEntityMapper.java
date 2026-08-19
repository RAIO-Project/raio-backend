package raio.wallet.adapter.rdb.mapper;

import org.mapstruct.Mapper;
import raio.wallet.domain.Wallet;
import raio.wallet.adapter.rdb.entity.WalletEntity;

@Mapper(componentModel = "spring")
public interface WalletEntityMapper {

    Wallet toDomain(WalletEntity entity);
    
    WalletEntity toEntity(Wallet wallet);
}
