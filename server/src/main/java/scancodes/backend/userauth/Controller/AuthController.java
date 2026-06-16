package scancodes.backend.userauth.Controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import scancodes.backend.userauth.Services.OtpVerificationService;
import scancodes.backend.userauth.Services.PasswordResetService;
import scancodes.backend.userauth.Services.UserService;
import scancodes.backend.userauth.dtos.ForgotPasswordRequest;
import scancodes.backend.userauth.dtos.RegisterRequest;
import scancodes.backend.userauth.dtos.ResendOtpRequest;
import scancodes.backend.userauth.dtos.ResetPasswordRequest;
import scancodes.backend.userauth.dtos.VerifyOtpRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final UserService userService;
  private final PasswordResetService passwordResetService;
  private final OtpVerificationService otpVerificationService;

  public AuthController(UserService userService,
                        PasswordResetService passwordResetService,
                        OtpVerificationService otpVerificationService) {
    this.userService = userService;
    this.passwordResetService = passwordResetService;
    this.otpVerificationService = otpVerificationService;
  }

  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
    var user = userService.register(request.username(), request.email(), request.password());
    otpVerificationService.issueOtp(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
      "message", "Registration successful. Enter the verification code sent to your email."
    ));
  }

  @PostMapping("/verify-otp")
  public ResponseEntity<Map<String, String>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
    otpVerificationService.verifyOtp(request.email(), request.otp());
    return ResponseEntity.ok(Map.of("message", "Email verified successfully. You can now log in."));
  }

  @PostMapping("/resend-otp")
  public ResponseEntity<Map<String, String>> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
    otpVerificationService.resendOtp(request.email());
    return ResponseEntity.ok(Map.of("message", "A new verification code has been sent to your email."));
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
}
