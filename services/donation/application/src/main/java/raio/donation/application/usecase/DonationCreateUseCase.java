package raio.donation.application.usecase;

public interface DonationCreateUseCase {

    /**
     * 후원 생성: (1) 포인트 차감 → (2) 후원 기록 → (3) 시청자 실시간 알림.
     * @return 생성된 후원 id (영속 전이면 null 가능)
     */
    Long create(DonationCreateCommand command);

    record DonationCreateCommand(
            Long streamId,
            Long senderId,
            Long receiverId,
            Long amount,
            String message,
            String senderNickname
    ) {}
}