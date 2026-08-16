package raio.donation.rdb.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import raio.donation.application.port.DonationCommandPort;
import raio.donation.domain.Donations;
import raio.donation.rdb.mapper.DonationsEntityMapper;
import raio.donation.rdb.repository.DonationsJpaRepository;

import static raio.donation.exception.DonationErrorCode.INTERNAL_ERROR;

@Repository
@RequiredArgsConstructor
public class DonationCommandAdapter implements DonationCommandPort {

    private final DonationsJpaRepository donationsJpaRepository;
    private final DonationsEntityMapper donationsEntityMapper;

    @Override
    public Donations save(Donations donations) {
        var entity = donationsEntityMapper.toEntity(donations);
        try {
            var saved = donationsJpaRepository.save(entity);
            donationsJpaRepository.flush();
            return donationsEntityMapper.toDomain(saved);
        } catch (DataAccessException e) {
            throw INTERNAL_ERROR.exception(e);
        }
    }
}
