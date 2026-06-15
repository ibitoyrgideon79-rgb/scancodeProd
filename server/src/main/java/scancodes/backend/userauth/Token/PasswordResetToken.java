package scancodes.backend.userauth.Token;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
@Table(name = "password_reset_tokens")
@Getter @Setter @NoArgsConstructor
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUserEntity user;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant usedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public boolean isValidAt(Instant now) {
        return usedAt == null && expiresAt.isAfter(now);
    }

    public void markUsed(Instant now) {
        this.usedAt = now;
    }

    public PasswordResetToken(String tokenHash, AppUserEntity user, int minutes) {
        this.tokenHash = tokenHash;
        this.user = user;
        this.expiresAt = Instant.now().plus(minutes, ChronoUnit.MINUTES);
    }

    public boolean isUsed() { return usedAt != null; }

    public boolean isValid() {
        return !isUsed() && Instant.now().isBefore(expiresAt);
    }
}
