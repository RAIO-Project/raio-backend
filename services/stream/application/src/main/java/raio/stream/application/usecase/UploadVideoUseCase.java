package raio.stream.application.usecase;

import raio.stream.application.command.UploadVideoCommand;

public interface UploadVideoUseCase {
    Long upload(UploadVideoCommand command);
}
