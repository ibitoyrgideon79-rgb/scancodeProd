package scancodes.backend.userauth.Token;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import scancodes.backend.userauth.Entity.AppUserEntity;

@Entity
@Table(name = "email_otp_codes")
@Getter
@Setter
@NoArgsConstructor
public class EmailOtpCode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "otp_hash", nullable = false, length = 64)
  private String otpHash;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private AppUserEntity user;

  @Column(nullable = false)
  private Instant expiresAt;

  private Instant usedAt;

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  public void markUsed(Instant now) {
    this.usedAt = now;
  }
}
