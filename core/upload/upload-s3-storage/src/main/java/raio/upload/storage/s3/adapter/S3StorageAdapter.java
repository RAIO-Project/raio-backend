package raio.upload.storage.s3.adapter;

import raio.upload.storage.s3.properties.S3StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class S3StorageAdapter {
    
    private final S3Client s3;
    private final S3Presigner s3Presigner;
    private final S3StorageProperties props;
    
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
        
        var presigned = s3Presigner.presignGetObject(presignRequest);
        
        return presigned.url().toString();
    }
    
    private String buildPublicUrl(String key) {
        if (props.publicBaseUrl() == null || props.publicBaseUrl().isBlank()) {
            // publicBaseUrl이 없으면 일단 key만 반환(프론트에서 다운로드 불가)
            // 운영에서는 publicBaseUrl 또는 presigned URL 중 하나는 반드시 제공해야 한다.
            return null;
        }
        String base = props.publicBaseUrl().endsWith("/") ? props.publicBaseUrl().substring(0, props.publicBaseUrl().length() - 1) : props.publicBaseUrl();
        return base + "/" + key;
    }
    
    public record UploadedObject(String key, String url, int bytes, String contentType) {
    }
}