package raio.stream.application.usecase;

import raio.stream.application.command.UploadVideoCommand;

public interface UploadVideoUseCase {
    record UploadVideoResult(Long videoId, String videoUrl) {}
    UploadVideoResult upload(UploadVideoCommand command);
}
