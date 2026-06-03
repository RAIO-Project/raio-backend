package raio.upload.storage.s3.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import raio.upload.file.FileStorage;
import raio.upload.storage.s3.properties.S3StorageProperties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Profile({"local", "prod"})
@Component
@RequiredArgsConstructor
public class S3StorageAdapter implements FileStorage {

    private final S3Client s3;
    private final S3Presigner s3Presigner;
    private final S3StorageProperties props;

    /** FileStorage 포트 구현 — InputStream을 S3에 업로드하고 접근 URL 반환 */
    @Override
    public String store(InputStream inputStream, String originalFileName, String contentType) {
        String ext = (originalFileName != null && originalFileName.contains("."))
                ? originalFileName.substring(originalFileName.lastIndexOf('.'))
                : "";
        String key = UUID.randomUUID() + ext;

        try {
            byte[] bytes = inputStream.readAllBytes();
            UploadedObject uploaded = upload(bytes, contentType, key);
            return uploaded.url() != null ? uploaded.url() : uploaded.key();
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 저장에 실패했습니다: " + key, e);
        }
    }

    public UploadedObject upload(byte[] bytes, String contentType, String key) {
        Objects.requireNonNull(bytes, "bytes");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(key, "key");

        var put = PutObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .contentType(contentType)
                .build();

        s3.putObject(put, RequestBody.fromBytes(bytes));

        String url = buildPublicUrl(key);

        return new UploadedObject(key, url, bytes.length, contentType);
    }

    public String generatePresignedGetUrl(String key, Duration validFor) {
        var getObjectRequest = GetObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .build();

        var presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(validFor)
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    private String buildPublicUrl(String key) {
        if (props.publicBaseUrl() == null || props.publicBaseUrl().isBlank()) {
            return null;
        }
        String base = props.publicBaseUrl().endsWith("/")
                ? props.publicBaseUrl().substring(0, props.publicBaseUrl().length() - 1)
                : props.publicBaseUrl();
        return base + "/" + key;
    }

    public record UploadedObject(String key, String url, int bytes, String contentType) {}
}
