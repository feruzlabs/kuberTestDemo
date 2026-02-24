package dev.feruzlabs.kubertestdemo.dto;

/**
 * CI/CD tekshiruvi javobi — pipeline va deploy holatini ko'rsatish uchun.
 */
public record CicdCheckResponse(
        String status,
        String stage,
        String version,
        java.time.Instant timestamp
) {}
