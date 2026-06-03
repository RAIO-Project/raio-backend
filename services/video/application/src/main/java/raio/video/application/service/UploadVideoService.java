package raio.video.application.service;

import org.springframework.stereotype.Service;
import raio.upload.file.FileStorage;
import raio.video.application.command.UploadVideoCommand;
import raio.video.application.port.VideoRepositoryPort;
import raio.video.application.usecase.UploadVideoUseCase;
import raio.video.domain.Videos;
import raio.video.domain.type.VideoStatus;

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
        // 파일 저장 후 경로 반환
        String storedPath = fileStorage.store(
                command.inputStream(),
                command.originalFileName(),
                command.contentType()
        );

        // 동영상 도메인 객체 생성 및 저장
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
