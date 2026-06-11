package raio.upload.storage.s3.config;

import raio.upload.storage.s3.properties.S3StorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Profile({"local", "prod"})
@Configuration
@EnableConfigurationProperties(S3StorageProperties.class)
public class S3StorageConfig {

    @Bean
    public S3Client s3Client(S3StorageProperties p) {
        var cred = AwsBasicCredentials.create(p.accessKey(), p.secretKey());

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(cred))
                .endpointOverride(URI.create(p.endpoint()))
                .region(Region.of(
                        p.region() == null || p.region().isBlank() ? "auto" : p.region()
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

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
                        p.region() == null || p.region().isBlank() ? "auto" : p.region()
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}
