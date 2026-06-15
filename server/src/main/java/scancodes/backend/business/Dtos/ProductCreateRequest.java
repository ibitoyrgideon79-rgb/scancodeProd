package scancodes.backend.business.Dtos;

import java.util.List;
import jakarta.validation.constraints.*;

public record ProductCreateRequest(
    @NotBlank @Size(max = 200) String name,
    @Size(max = 2000) String description,
    @Positive double price,
    @PositiveOrZero int stock,
    List<String> mediaUrls,
    @Size(max = 100) String category,
    boolean isPopular
) {}
