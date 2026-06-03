package raio.video.application.usecase;

import raio.video.application.command.UploadVideoCommand;

public interface UploadVideoUseCase {
    Long upload(UploadVideoCommand command);
}
