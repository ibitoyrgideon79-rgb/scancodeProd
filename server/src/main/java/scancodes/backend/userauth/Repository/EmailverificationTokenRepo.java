package scancodes.backend.userauth.Repository;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import scancodes.backend.userauth.Token.EmailVerificationToken;

public interface EmailverificationTokenRepo extends JpaRepository<EmailVerificationToken, Long> {

  @Query("""
    select t from EmailVerificationToken t
    where t.tokenHash = :hash
      and t.usedAt is null
      and t.expiresAt > :now
  """)
  Optional<EmailVerificationToken> findValid(@Param("hash") String hash, @Param("now") Instant now);

  @Modifying
  @Query("""
    delete from EmailVerificationToken t
    where t.user.id = :userId
      and t.usedAt is null
  """)
  int deleteActiveByUserId(@Param("userId") Long userId);
}
