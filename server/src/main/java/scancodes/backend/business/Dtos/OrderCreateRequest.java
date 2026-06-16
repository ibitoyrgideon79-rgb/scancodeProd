package scancodes.backend.business.Dtos;

import java.util.List;
import jakarta.validation.constraints.*;

public record OrderCreateRequest(
    @Size(max = 200) String customerName,
    @Size(max = 50) String customerPhone,
    @Email @Size(max = 255) String customerEmail,
    @NotNull List<OrderItemRequest> items,
    @PositiveOrZero double subtotal,
    @PositiveOrZero double vat,
    @PositiveOrZero double delivery,
    @Positive double total
) {
    public record OrderItemRequest(
        String id,
        @NotBlank String name,
        @Positive int qty,
        @Positive double price
    ) {}
}
