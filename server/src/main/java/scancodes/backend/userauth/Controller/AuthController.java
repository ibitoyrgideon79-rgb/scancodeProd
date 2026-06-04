package scancodes.backend.userauth.Controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import scancodes.backend.userauth.Services.EmailVerificationService;
import scancodes.backend.userauth.Services.PasswordResetService;
import scancodes.backend.userauth.Services.UserService;
import scancodes.backend.userauth.dtos.ForgotPasswordRequest;
import scancodes.backend.userauth.dtos.RegisterRequest;
import scancodes.backend.userauth.dtos.ResetPasswordRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final UserService userService;
  private final PasswordResetService passwordResetService;
  private final EmailVerificationService emailVerificationService;

  public AuthController(UserService userService,
                        PasswordResetService passwordResetService,
                        EmailVerificationService emailVerificationService) {
    this.userService = userService;
    this.passwordResetService = passwordResetService;
    this.emailVerificationService = emailVerificationService;
  }

  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
    userService.register(request.username(), request.email(), request.password());
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
      "message", "Registration successful. Check your email for verification link."
    ));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    passwordResetService.requestReset(request.email());
    return ResponseEntity.ok(Map.of(
      "message", "If the account exists, a reset link has been sent."
    ));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    if (!request.newPassword().equals(request.confirmPassword())) {
      throw new IllegalArgumentException("Passwords do not match");
    }
    passwordResetService.resetPassword(request.token(), request.newPassword());
    return ResponseEntity.ok(Map.of("message", "Password reset successful"));
  }

  @GetMapping("/verify")
  public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam("token") String token) {
    emailVerificationService.verifyEmail(token);
    return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }
}
