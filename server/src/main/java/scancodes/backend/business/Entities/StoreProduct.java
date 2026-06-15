package scancodes.backend.business.Entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "store_products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StoreProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "storefront_id", nullable = false)
    private Long storefrontId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int stock;

    @Column(name = "is_delisted", nullable = false)
    private boolean isDelisted = false;

    @Column(name = "media_urls", columnDefinition = "text")
    private String mediaUrls; // JSON array of URLs

    @Column(length = 100)
    private String category;

    @Column(name = "is_popular")
    private boolean isPopular = false;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
