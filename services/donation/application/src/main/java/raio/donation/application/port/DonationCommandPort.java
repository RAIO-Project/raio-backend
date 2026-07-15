package raio.donation.application.port;

import raio.donation.domain.Donations;

/** 후원 영속화 포트 (driven). */
public interface DonationCommandPort {

    /** 후원 저장. 채번된 id 를 포함한 후원을 반환한다. */
    Donations save(Donations donations);
}
