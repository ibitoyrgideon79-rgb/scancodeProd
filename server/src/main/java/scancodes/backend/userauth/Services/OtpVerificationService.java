package scancodes.backend.userauth.Services;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import scancodes.backend.userauth.Entity.AppUserEntity;
import scancodes.backend.userauth.Repository.EmailOtpRepository;
import scancodes.backend.userauth.Repository.UserRepository;
import scancodes.backend.userauth.Token.EmailOtpCode;
import scancodes.backend.userauth.Token.SecureToken;

@Service
public class OtpVerificationService {

  private static final SecureRandom RNG = new SecureRandom();
  private static final Duration OTP_TTL = Duration.ofMinutes(10);

  private final EmailOtpRepository otpRepository;
  private final UserRepository userRepository;
  private final EmailSender emailSender;

  public OtpVerificationService(EmailOtpRepository otpRepository,
                                UserRepository userRepository,
                                EmailSender emailSender) {
    this.otpRepository = otpRepository;
    this.userRepository = userRepository;
    this.emailSender = emailSender;
  }

  @Transactional
  public void issueOtp(AppUserEntity user) {
    otpRepository.deleteActiveByUserId(user.getId());

    String otp = String.format("%06d", RNG.nextInt(1_000_000));
    String otpHash = SecureToken.sha256Hex(otp);

    EmailOtpCode code = new EmailOtpCode();
    code.setUser(user);
    code.setOtpHash(otpHash);
    code.setExpiresAt(Instant.now().plus(OTP_TTL));
    otpRepository.save(code);

    String htmlBody = """
        <div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;">
          <h2 style="color:#16a34a;">Verify your ScanCode account</h2>
          <p>Your one-time verification code is:</p>
          <p style="font-size:32px;font-weight:bold;letter-spacing:8px;color:#111;">%s</p>
          <p>This code expires in 10 minutes. If you did not create an account, you can ignore this email.</p>
        </div>
        """.formatted(otp);

    emailSender.send(user.getEmail(), "Your ScanCode verification code", htmlBody);
  }

  @Transactional
  public void resendOtp(String email) {
    String normalizedEmail = email.toLowerCase(Locale.ROOT);
    AppUserEntity user = userRepository.findByEmail(normalizedEmail)
        .orElseThrow(() -> new IllegalArgumentException("No account found for this email"));

    if (user.isEnabled()) {
      throw new IllegalArgumentException("Account is already verified");
    }

    issueOtp(user);
  }

  @Transactional
  public void verifyOtp(String email, String otp) {
    String normalizedEmail = email.toLowerCase(Locale.ROOT);
    AppUserEntity user = userRepository.findByEmail(normalizedEmail)
        .orElseThrow(() -> new IllegalArgumentException("No account found for this email"));

    if (user.isEnabled()) {
      return;
    }

    String normalizedOtp = otp == null ? "" : otp.trim();
    if (!normalizedOtp.matches("\\d{6}")) {
      throw new IllegalArgumentException("Invalid verification code");
    }

    String otpHash = SecureToken.sha256Hex(normalizedOtp);
    Instant now = Instant.now();

    EmailOtpCode code = otpRepository.findValid(otpHash, now)
        .orElseThrow(() -> new IllegalArgumentException("Invalid or expired verification code"));

    if (!code.getUser().getId().equals(user.getId())) {
      throw new IllegalArgumentException("Invalid verification code");
    }

    user.setEnabled(true);
    code.markUsed(now);
  }
}
