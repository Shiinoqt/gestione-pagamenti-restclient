package com.its.gestionepagamentirestclient.controller;

import com.its.gestionepagamentirestclient.dto.PaymentRequest;
import com.its.gestionepagamentirestclient.dto.PaymentResponse;
import com.its.gestionepagamentirestclient.security.RequiresAdmin;
import com.its.gestionepagamentirestclient.service.PaymentService;
import com.its.gestionepagamentirestclient.service.PaymentUploadService;
import com.its.gestionepagamentirestclient.service.ReceiptDownloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private static final String USER_ID_HEADER = "Auth-User-Id";

    private final PaymentService paymentService;
    private final ReceiptDownloadService receiptDownloadService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<PaymentResponse> process(@Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.processPayment(paymentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/process-with-check", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentResponse> processWithCheck(
            @RequestPart("payment") @Valid PaymentRequest paymentRequest,
            @RequestPart("file") MultipartFile file) {

        PaymentResponse response = paymentService.processPaymentWithCheck(paymentRequest, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path = "/orders/{id}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrderId(@PathVariable UUID id) {
        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId(id);
        return ResponseEntity.ok(responses);
    }

    @GetMapping(path = "/download/{id}")
    public ResponseEntity<Void> downloadReceipt(
            @PathVariable String id,
            @RequestHeader(USER_ID_HEADER) List<String> userIdValues) {

        String userId = userIdValues.stream()
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .orElse(null);

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        URL presignedUrl = receiptDownloadService.createDownloadUrl(userId.trim(), id, Duration.ofMinutes(2));

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(presignedUrl.toString()))
                .build();
    }
}