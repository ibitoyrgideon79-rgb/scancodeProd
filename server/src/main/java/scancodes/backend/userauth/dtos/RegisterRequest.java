package scancodes.backend.userauth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record RegisterRequest(
  @NotBlank @Size(min = 3, max = 50) String username,
  @NotBlank @Email String email,
  @NotBlank @Size(min = 8, max = 200) String password
) {}
