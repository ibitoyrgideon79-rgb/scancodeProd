package scancodes.backend.business.Dtos;

import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import scancodes.backend.business.Entities.BusinessType;

public record BusinessCreateRequest(
    @NotNull BusinessType businessType,
    @NotBlank @Size(max = 120) String name,
    @Size(max = 500) String description,
    @Size(max = 500) String logoUrl,
    @Size(max = 500) String bannerUrl,
    @NotNull Map<String, Object> data
) {}
