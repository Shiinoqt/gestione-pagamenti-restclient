package com.its.gestionepagamentirestclient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentUploadService {
    private final S3AsyncClient s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public CompletableFuture<PutObjectResponse> uploadCheck(String userId, String orderId, byte[] pdfBytes) {
        String key = buildReceiptKey(userId, orderId);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/pdf")
                .contentLength((long) pdfBytes.length)
                .build();

        return s3Client.putObject(request, AsyncRequestBody.fromBytes(pdfBytes));
    }

    public String buildReceiptKey(String userId, String orderId) {
        return String.format("p.rebong/%s/check/check-%s.pdf", userId, orderId);
    }
}