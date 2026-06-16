package scancodes.backend.business.Dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

public record StoreConfigRequest(
    @Min(0) @Max(100) double vatRate,
    @PositiveOrZero double deliveryFee
) {}
