package scancodes.backend.userauth.Repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import scancodes.backend.userauth.Token.EmailOtpCode;

public interface EmailOtpRepository extends JpaRepository<EmailOtpCode, Long> {

  @Query("""
    select o from EmailOtpCode o
    where o.otpHash = :hash
      and o.usedAt is null
      and o.expiresAt > :now
  """)
  Optional<EmailOtpCode> findValid(@Param("hash") String hash, @Param("now") Instant now);

  @Modifying
  @Query("""
    delete from EmailOtpCode o
    where o.user.id = :userId
      and o.usedAt is null
  """)
  int deleteActiveByUserId(@Param("userId") Long userId);
}
