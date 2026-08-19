package raio.stream.web.dto;

import org.springframework.web.multipart.MultipartFile;
import raio.stream.application.command.UploadVideoCommand;

import java.io.IOException;

public class VideoDto {

    public record UploadRequest(MultipartFile file, String title) {
        public UploadVideoCommand toCommand(Long uploaderId) throws IOException {
            return new UploadVideoCommand(
                    uploaderId,
                    title,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getInputStream()
            );
        }
    }

    public record UploadResponse(Long videoId, String videoUrl) {
    }
}
