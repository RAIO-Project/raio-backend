package raio.stream.application.service;

import org.springframework.stereotype.Service;
import raio.upload.file.FileStorage;
import raio.stream.application.command.UploadVideoCommand;
import raio.stream.application.port.VideoRepositoryPort;
import raio.stream.application.usecase.UploadVideoUseCase;
import raio.stream.domain.Videos;
import raio.stream.domain.type.VideoStatus;

@Service
public class UploadVideoService implements UploadVideoUseCase {

    private final VideoRepositoryPort videoRepository;
    private final FileStorage fileStorage;

    public UploadVideoService(VideoRepositoryPort videoRepository, FileStorage fileStorage) {
        this.videoRepository = videoRepository;
        this.fileStorage = fileStorage;
    }

    @Override
    public Long upload(UploadVideoCommand command) {
        String storedPath = fileStorage.store(
                command.inputStream(),
                command.originalFileName(),
                command.contentType()
        );

        Videos video = Videos.builder()
                .uploaderId(command.uploaderId())
                .title(command.title())
                .originalFileName(command.originalFileName())
                .storedFilePath(storedPath)
                .fileSize(command.fileSize())
                .contentType(command.contentType())
                .status(VideoStatus.READY)
                .build();

        return videoRepository.save(video).getId();
    }
}
