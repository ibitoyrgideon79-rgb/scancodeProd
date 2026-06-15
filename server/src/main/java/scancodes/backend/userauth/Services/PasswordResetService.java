package scancodes.backend.userauth.Services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scancodes.backend.config.AppAuthProperties;
import scancodes.backend.userauth.Entity.AppUserEntity;
import scancodes.backend.userauth.Repository.UserRepository;
import scancodes.backend.userauth.Token.PasswordResetToken;
import scancodes.backend.userauth.Token.PasswordResetTokenRepository;
import scancodes.backend.userauth.Token.SecureToken;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class PasswordResetService {
  private final UserRepository userRepository;
  private final PasswordResetTokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailSender emailSender;
  private final AppAuthProperties authProperties;

  public PasswordResetService(UserRepository userRepository,
                              PasswordResetTokenRepository tokenRepository,
                              PasswordEncoder passwordEncoder,
                              EmailSender emailSender,
                              AppAuthProperties authProperties) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailSender = emailSender;
    this.authProperties = authProperties;
  }

  @Transactional
  public void requestReset(String email) {
    var normalizedEmail = email.toLowerCase(Locale.ROOT);
    var userOpt = userRepository.findByEmail(normalizedEmail);

    if (userOpt.isEmpty()) {
      return; // Don't reveal if email exists
    }

    var user = userOpt.get();
    tokenRepository.deleteActiveByUserId(user.getId());

    var rawToken = SecureToken.generateUrlSafe(32);
    var tokenHash = SecureToken.sha256Hex(rawToken);

    var token = new PasswordResetToken();
    token.setUser(user);
    token.setTokenHash(tokenHash);
    token.setCreatedAt(Instant.now());
    token.setExpiresAt(Instant.now().plus(Duration.ofMinutes(15)));
    tokenRepository.save(token);

    var encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
    var link = authProperties.buildUrl("/auth/reset-password?token=" + encoded);
    emailSender.send(normalizedEmail, "Reset your password", "Click: " + link);
  }

  @Transactional
  public void resetPassword(String rawToken, String newPassword) {
    var tokenHash = SecureToken.sha256Hex(rawToken);
    var now = Instant.now();
    var token = tokenRepository.findValid(tokenHash, now)
      .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

    var user = token.getUser();
    user.setPasswordHash(passwordEncoder.encode(newPassword));

    token.markUsed(now);
    tokenRepository.deleteActiveByUserId(user.getId());
    emailSender.send(user.getEmail(), "Password changed", "Your password was changed successfully.");
  }
}
