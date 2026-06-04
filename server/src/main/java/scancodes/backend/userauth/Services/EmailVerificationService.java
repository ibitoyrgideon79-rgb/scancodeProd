package scancodes.backend.userauth.Services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scancodes.backend.config.AppAuthProperties;
import scancodes.backend.userauth.Entity.AppUserEntity;
import scancodes.backend.userauth.Token.EmailVerificationToken;
import scancodes.backend.userauth.Repository.EmailverificationTokenRepo;
import scancodes.backend.userauth.Token.SecureToken;

@Service
public class EmailVerificationService {
  private final EmailverificationTokenRepo tokenRepository;
  private final EmailSender emailSender;
  private final AppAuthProperties authProperties;

  public EmailVerificationService(EmailverificationTokenRepo tokenRepository,
                                  EmailSender emailSender,
                                  AppAuthProperties authProperties) {
    this.tokenRepository = tokenRepository;
    this.emailSender = emailSender;
    this.authProperties = authProperties;
  }

  @Transactional
  public void issueVerification(AppUserEntity user) {
    tokenRepository.deleteActiveByUserId(user.getId());

    String rawToken = SecureToken.generateUrlSafe(32);
    String tokenHash = SecureToken.sha256Hex(rawToken);

    EmailVerificationToken token = new EmailVerificationToken();
    token.setUser(user);
    token.setTokenHash(tokenHash);
    token.setExpiresAt(Instant.now().plus(Duration.ofHours(24)));
    tokenRepository.save(token);

    String encoded = URLEncoder.encode(rawToken, StandardCharsets.UTF_8);
    String link = authProperties.buildUrl("/auth/verify?token=" + encoded);

    emailSender.send(
      user.getEmail(),
      "Verify your email",
      "Welcome! Verify your account with this link:\n" + link
    );
  }

  @Transactional
  public void verifyEmail(String rawToken) {
    String tokenHash = SecureToken.sha256Hex(rawToken);
    Instant now = Instant.now();

    EmailVerificationToken token = (EmailVerificationToken) tokenRepository.findValid(tokenHash, now)
      .orElseThrow(() -> new IllegalArgumentException("Invalid or expired verification token"));

    AppUserEntity user = token.getUser();
    user.setEnabled(true);
    token.markUsed(now);
  }
}
