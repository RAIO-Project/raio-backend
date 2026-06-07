package raio.stream.application.command;

import java.io.InputStream;

public record UploadVideoCommand(
        Long uploaderId,
        String title,
        String originalFileName,
        String contentType,
        long fileSize,
        InputStream inputStream
) {
}
