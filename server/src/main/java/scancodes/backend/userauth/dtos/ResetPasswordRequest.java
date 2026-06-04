package scancodes.backend.userauth.dtos;

public record ResetPasswordRequest(
  @NotBlank String token,
  @NotBlank @Size(min = 8, max = 200) String newPassword,
  @NotBlank @Size(min = 8, max = 200) String confirmPassword
) {}
