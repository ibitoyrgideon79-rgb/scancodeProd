package scancodes.backend.userauth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record ForgotPasswordRequest(
  @NotBlank @Email String email
) {}
