package com.senla.ProductService.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Component
public class YandexCloudUtil {

    @Value("${yandex.cloud.key.id}")
    private String keyId;
    @Value("${yandex.cloud.secret.key}")
    private String secretKey;
    @Value("${yandex.cloud.region}")
    private String region;
    @Value("${yandex.cloud.bucket.name}")
    private String bucket;
    @Value("${s3.endpoint}")
    private String endpoint;

    private S3Client s3Client;

    public YandexCloudUtil() {
    }

    @PostConstruct
    public void init() {
        AwsCredentials credentials = AwsBasicCredentials.create(keyId, secretKey);

        s3Client = S3Client.builder()
                .httpClient(ApacheHttpClient.create())
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public String saveImageToStorage(MultipartFile photo, String folderName){
        try {
            String key =  folderName + UUID.randomUUID() + "_" + photo.getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(photo.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(photo.getBytes()));

            return s3Client.utilities()
                    .getUrl(GetUrlRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build())
                    .toExternalForm();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
