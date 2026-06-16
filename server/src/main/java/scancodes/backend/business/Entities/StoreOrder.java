package scancodes.backend.business.Entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "store_orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StoreOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "storefront_id", nullable = false)
    private Long storefrontId;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @Column(name = "customer_phone", length = 50)
    private String customerPhone;

    @Column(name = "customer_email", length = 255)
    private String customerEmail;

    @Column(name = "order_items", columnDefinition = "text", nullable = false)
    private String orderItems; // JSON array

    @Column(nullable = false)
    private double subtotal;

    @Column(nullable = false)
    private double vat;

    @Column(nullable = false)
    private double delivery;

    @Column(nullable = false)
    private double total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum OrderStatus {
        PENDING, CUSTOMER_NOTIFIED, CONFIRMED
    }
}
