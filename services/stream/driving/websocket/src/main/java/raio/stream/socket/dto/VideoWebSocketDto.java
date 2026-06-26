package raio.stream.socket.dto;

public class VideoWebSocketDto {

    public record VideoSyncCommand(String videoUrl, double currentTime, boolean playing) {}
}
