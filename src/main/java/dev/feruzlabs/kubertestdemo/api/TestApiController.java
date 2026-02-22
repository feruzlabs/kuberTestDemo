package dev.feruzlabs.kubertestdemo.api;

import dev.feruzlabs.kubertestdemo.dto.TestApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/test")
@Tag(name = "Test API", description = "Tekshirish uchun test endpoint")
public class TestApiController {

    @Value("${spring.application.name:kuberTestDemo}")
    private String appName;

    @GetMapping
    @Operation(summary = "Tekshirish", description = "Server ishlayotganini va versiyani qaytaradi")
    public ResponseEntity<TestApiResponse> get() {
        var response = new TestApiResponse(
                "OK — " + appName + " ishlayapti",
                "0.0.1-SNAPSHOT",
                Instant.now()
        );
        return ResponseEntity.ok(response);
    }
}
