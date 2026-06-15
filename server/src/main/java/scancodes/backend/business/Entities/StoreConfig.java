package scancodes.backend.business.Entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "store_configs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StoreConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "storefront_id", nullable = false, unique = true)
    private Long storefrontId;

    @Column(name = "vat_rate", nullable = false)
    private double vatRate = 0.075; // 7.5%

    @Column(name = "delivery_fee", nullable = false)
    private double deliveryFee = 2000;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
