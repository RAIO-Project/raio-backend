package raio.donation.application.port;

import raio.donation.domain.Donations;

public interface DonationBroadcastPort {
    /**
     * 후원 발생 시 방송방 시청자들에게 실시간 알림
     */
    void broadcastDonation(Long streamId, Donations donations, String senderNickname);
}
