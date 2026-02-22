package dev.feruzlabs.kubertestdemo.dto;

/**
 * Test API javobi — tekshirish uchun.
 */
public record TestApiResponse(
        String message,
        String version,
        java.time.Instant timestamp
) {}
