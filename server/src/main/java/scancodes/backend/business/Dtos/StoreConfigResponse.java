package scancodes.backend.business.Dtos;

public record StoreConfigResponse(
    Long id,
    Long storefrontId,
    double vatRate,
    double deliveryFee
) {}
