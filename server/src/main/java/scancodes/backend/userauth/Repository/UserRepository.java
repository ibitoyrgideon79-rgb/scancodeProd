package scancodes.backend.userauth.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import scancodes.backend.userauth.Entity.AppUserEntity;


public interface UserRepository extends JpaRepository<AppUserEntity, Long> {
    Optional<AppUserEntity> findByUsername(String username);
    Optional<AppUserEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    AppUserEntity save(AppUserEntity user);
    
}