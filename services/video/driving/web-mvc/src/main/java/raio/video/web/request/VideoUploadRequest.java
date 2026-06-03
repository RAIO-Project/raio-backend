package raio.video.web.request;

import org.springframework.web.multipart.MultipartFile;
import raio.video.application.command.UploadVideoCommand;

import java.io.IOException;

public record VideoUploadRequest(
        MultipartFile file,
        String title
) {
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
