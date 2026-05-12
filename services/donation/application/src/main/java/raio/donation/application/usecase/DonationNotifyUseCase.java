package raio.donation.application.usecase;

import raio.donation.domain.Donations;

public interface DonationNotifyUseCase {
    /**
     * 후원 알림을 방송방 시청자들에게 송출
     */
    void notifyDonation(Donations donations, String senderNickname);
}
