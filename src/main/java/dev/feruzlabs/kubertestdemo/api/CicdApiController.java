package dev.feruzlabs.kubertestdemo.api;

import dev.feruzlabs.kubertestdemo.dto.CicdCheckResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/cicd")
@Tag(name = "CI/CD API", description = "Pipeline va deploy tekshiruvi")
public class CicdApiController {

    private static final String VERSION = "0.0.1-SNAPSHOT";

    @GetMapping
    @Operation(summary = "CI/CD holati", description = "Deploy muvaffaqiyatli bo'lganini tekshirish uchun")
    public ResponseEntity<CicdCheckResponse> check() {
        var response = new CicdCheckResponse(
                "OK",
                "deployed",
                VERSION,
                Instant.now()
        );
        return ResponseEntity.ok(response);
    }
}
