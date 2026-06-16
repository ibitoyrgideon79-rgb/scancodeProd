package scancodes.backend.business.Dtos;

import java.time.LocalDateTime;

public record OrderResponse(
    Long id,
    Long storefrontId,
    String customerName,
    String customerPhone,
    String customerEmail,
    String orderItems,
    double subtotal,
    double vat,
    double delivery,
    double total,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
