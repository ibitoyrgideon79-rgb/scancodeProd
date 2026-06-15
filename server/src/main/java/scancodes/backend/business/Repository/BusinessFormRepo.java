package scancodes.backend.business.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import scancodes.backend.business.Entities.BusinessFormEntities;

@Repository
public interface BusinessFormRepo extends JpaRepository<BusinessFormEntities, Long> {
    boolean existsBySlug(String slug);
    Optional<BusinessFormEntities> findByIdAndUserId(Long id, Long userId);
    Optional<BusinessFormEntities> findBySlugAndIsActiveTrue(String slug);
    List<BusinessFormEntities> findAllByUserId(Long userId);
}
