package scancodes.backend.userauth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
  @NotBlank String token,
  @NotBlank @Size(min = 8, max = 200) String newPassword,
  @NotBlank @Size(min = 8, max = 200) String confirmPassword
) {}
