package raio.donation.domain.type;

import java.util.EnumSet;
import java.util.Set;

public enum DonationStatus {
    FAILED,
    COMPLETED;
    
    private static final Set<DonationStatus> ALL_DONATION_STATUS = EnumSet.of(FAILED, COMPLETED);
    
    public static Set<DonationStatus> getAllDonationStatus() {
        return ALL_DONATION_STATUS;
    }
}

