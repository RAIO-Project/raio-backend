package raio.stream.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import raio.upload.file.FileStorage;
import raio.stream.application.command.UploadVideoCommand;
import raio.stream.application.port.VideoRepositoryPort;
import raio.stream.application.usecase.UploadVideoUseCase;
import raio.stream.domain.Videos;
import raio.stream.domain.type.VideoStatus;

@Slf4j
@Service
public class UploadVideoService implements UploadVideoUseCase {

    private final VideoRepositoryPort videoRepository;
    private final FileStorage fileStorage;

    public UploadVideoService(VideoRepositoryPort videoRepository, FileStorage fileStorage) {
        this.videoRepository = videoRepository;
        this.fileStorage = fileStorage;
    }

    @Override
    public UploadVideoResult upload(UploadVideoCommand command) {
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

        try {
            Videos saved = videoRepository.save(video);
            return new UploadVideoResult(saved.getId(), saved.getStoredFilePath());
        } catch (Exception e) {
            // DB 저장 실패 시 S3에 올라간 파일을 삭제해 고아 파일 방지
            try {
                fileStorage.delete(storedPath);
            } catch (Exception deleteEx) {
                log.error("[UploadVideo] DB 저장 실패 후 S3 파일 삭제도 실패. 수동 정리 필요. path={}", storedPath, deleteEx);
            }
            throw e;
        }
    }
}
