package raio.upload.storage.s3.config;

import raio.upload.storage.s3.properties.S3StorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * S3 Storage 관련 인프라 설정.
 * Railway Storage Bucket은 AWS S3와 100% 동일한 리전 체계를 사용하지 않으므로
 * endpointOverride 설정이 필수
 */
@Configuration
@EnableConfigurationProperties(S3StorageProperties.class)
public class S3StorageConfig {
    
    /**
     * 실제 S3 객체 업로드/다운로드를 담당하는 Client Bean.
     * - putObject (파일 업로드)
     * - getObject (파일 다운로드 - 비권장 방식)
     * - deleteObject (파일 삭제)
     */
    @Bean
    public S3Client s3Client(S3StorageProperties p) {
        var cred = AwsBasicCredentials.create(p.accessKey(), p.secretKey());
        
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(cred))
                // Railway S3 endpoint (필수)
                .endpointOverride(URI.create(p.endpoint()))
                // Railway는 일반 AWS region이 아닐 수 있으므로 auto fallback
                .region(Region.of(
                        p.region() == null || p.region().isBlank()
                                ? "auto"
                                : p.region()
                ))
                .build();
    }
    
    /**
     * Presigned URL 생성을 담당하는 Bean.
     * 서버가 S3 객체를 직접 전달하지 않고
     * 일정 시간 유효한 접근 URL을 생성하여
     * 클라이언트가 S3에 직접 접근하도록 하기 위함.
     */
    @Bean
    public S3Presigner s3Presigner(S3StorageProperties p) {
        return S3Presigner.builder()
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(p.accessKey(), p.secretKey())
                        )
                )
                .endpointOverride(URI.create(p.endpoint()))
                .region(Region.of(
                        p.region() == null || p.region().isBlank()
                                ? "auto"
                                : p.region()
                ))
                .build();
    }
}