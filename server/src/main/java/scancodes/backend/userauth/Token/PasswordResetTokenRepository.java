package scancodes.backend.userauth.Token;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
  @Query("""
    select t from PasswordResetToken t
    where t.tokenHash = :hash
      and t.usedAt is null
      and t.expiresAt > :now
  """)
  Optional<PasswordResetToken> findValid(@Param("hash") String hash, @Param("now") Instant now);

  @Modifying
  @Query("delete from PasswordResetToken t where t.user.id = :userId and t.usedAt is null")
  void deleteActiveByUserId(@Param("userId") Long userId);
}

