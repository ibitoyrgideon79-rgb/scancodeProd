package scancodes.backend.business.Dtos;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
    Long id,
    Long storefrontId,
    String name,
    String description,
    double price,
    int stock,
    boolean isDelisted,
    List<String> mediaUrls,
    String category,
    boolean isPopular,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
