package scancodes.backend.business.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scancodes.backend.business.Entities.StoreConfig;

@Repository
public interface StoreConfigRepository extends JpaRepository<StoreConfig, Long> {
    Optional<StoreConfig> findByStorefrontId(Long storefrontId);
}
