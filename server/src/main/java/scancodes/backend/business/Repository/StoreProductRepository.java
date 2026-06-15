package scancodes.backend.business.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scancodes.backend.business.Entities.StoreProduct;

@Repository
public interface StoreProductRepository extends JpaRepository<StoreProduct, Long> {
    List<StoreProduct> findAllByStorefrontIdAndIsDelistedFalse(Long storefrontId);
    List<StoreProduct> findAllByStorefrontId(Long storefrontId);
}
