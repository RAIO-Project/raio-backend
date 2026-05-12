package raio.donation.application.service;

import lombok.RequiredArgsConstructor;
import raio.donation.application.port.DonationBroadcastPort;
import raio.donation.application.usecase.DonationNotifyUseCase;
import raio.donation.domain.Donations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DonationNotifyService implements DonationNotifyUseCase {

    private final DonationBroadcastPort donationBroadcastPort;

    @Override
    public void notifyDonation(Donations donations, String senderNickname) {
        donationBroadcastPort.broadcastDonation(
                donations.getStreamId(),
                donations,
                senderNickname
        );
    }
}