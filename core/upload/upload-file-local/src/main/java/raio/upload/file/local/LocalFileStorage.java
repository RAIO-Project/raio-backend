package raio.upload.file.local;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import raio.upload.file.FileStorage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Profile("local-fs")
@Component
@EnableConfigurationProperties(FileStorageProperties.class)
public class LocalFileStorage implements FileStorage {

    private final Path uploadDir;

    public LocalFileStorage(FileStorageProperties properties) {
        this.uploadDir = Paths.get(properties.path()).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadDir);
    }

    @Override
    public String store(InputStream inputStream, String originalFileName, String contentType) {
        String ext = (originalFileName != null && originalFileName.contains("."))
                ? originalFileName.substring(originalFileName.lastIndexOf('.'))
                : "";
        String storedName = UUID.randomUUID() + ext;
        Path targetPath = uploadDir.resolve(storedName);

        try {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다: " + storedName, e);
        }

        return targetPath.toString();
    }
}
