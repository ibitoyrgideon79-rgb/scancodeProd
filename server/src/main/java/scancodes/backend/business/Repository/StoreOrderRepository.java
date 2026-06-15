package scancodes.backend.business.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scancodes.backend.business.Entities.StoreOrder;

@Repository
public interface StoreOrderRepository extends JpaRepository<StoreOrder, Long> {
    List<StoreOrder> findAllByStorefrontIdOrderByCreatedAtDesc(Long storefrontId);
}
