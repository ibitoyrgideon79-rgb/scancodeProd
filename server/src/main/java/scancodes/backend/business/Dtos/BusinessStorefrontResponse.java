package scancodes.backend.business.Dtos;

import java.time.LocalDateTime;
import java.util.Map;
import scancodes.backend.business.Entities.BusinessType;

public record BusinessStorefrontResponse(
    Long id,
    Long userId,
    BusinessType businessType,
    String slug,
    String publicUrl,
    String name,
    String description,
    String logoUrl,
    String bannerUrl,
    Map<String, Object> data,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
