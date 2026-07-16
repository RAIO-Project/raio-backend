package raio.stream.socket.relay;

import raio.socket.relay.RelayMessage;

import java.time.Instant;

public record VideoMessage(
        String type,
        String streamId,
        String videoUrl,
        double currentTime,
        boolean playing,
        Instant occurredAt
) implements RelayMessage {}
