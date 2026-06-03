package raio.upload.file.local;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("file.storage.local")
public record FileStorageProperties(String path) {
    public FileStorageProperties {
        if (path == null) path = "/tmp/raio/files";
    }
}
