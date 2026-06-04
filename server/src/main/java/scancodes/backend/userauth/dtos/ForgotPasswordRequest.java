package scancodes.backend.userauth.dtos;

public record ForgotPasswordRequest(
  @NotBlank @Email String email
) {}
