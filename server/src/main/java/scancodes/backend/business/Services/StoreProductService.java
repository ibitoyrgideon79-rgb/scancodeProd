package scancodes.backend.business.Services;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import scancodes.backend.business.Dtos.ProductCreateRequest;
import scancodes.backend.business.Dtos.ProductResponse;
import scancodes.backend.business.Entities.StoreProduct;
import scancodes.backend.business.Repository.BusinessFormRepo;
import scancodes.backend.business.Repository.StoreProductRepository;
import scancodes.backend.userauth.Repository.UserRepository;

@Service
public class StoreProductService {

    private final StoreProductRepository productRepo;
    private final BusinessFormRepo storefrontRepo;
    private final UserRepository userRepo;
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    public StoreProductService(StoreProductRepository productRepo,
                               BusinessFormRepo storefrontRepo,
                               UserRepository userRepo) {
        this.productRepo = productRepo;
        this.storefrontRepo = storefrontRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public ProductResponse createProduct(Long storefrontId, ProductCreateRequest request, String username) {
        verifyOwnership(storefrontId, username);

        var product = new StoreProduct();
        product.setStorefrontId(storefrontId);
        product.setName(request.name().trim());
        product.setDescription(request.description() != null ? request.description().trim() : null);
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setMediaUrls(writeJson(request.mediaUrls()));
        product.setCategory(request.category());
        product.setPopular(request.isPopular());
        product.setDelisted(false);

        return toResponse(productRepo.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getPublicProducts(Long storefrontId) {
        return productRepo.findAllByStorefrontIdAndIsDelistedFalse(storefrontId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(Long storefrontId, String username) {
        verifyOwnership(storefrontId, username);
        return productRepo.findAllByStorefrontId(storefrontId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProductResponse toggleDelist(Long storefrontId, Long productId, String username) {
        verifyOwnership(storefrontId, username);
        var product = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        if (!product.getStorefrontId().equals(storefrontId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Product does not belong to this storefront");
        }
        product.setDelisted(!product.isDelisted());
        return toResponse(productRepo.save(product));
    }

    @Transactional
    public void deleteProduct(Long storefrontId, Long productId, String username) {
        verifyOwnership(storefrontId, username);
        var product = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        if (!product.getStorefrontId().equals(storefrontId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Product does not belong to this storefront");
        }
        productRepo.delete(product);
    }

    private void verifyOwnership(Long storefrontId, String username) {
        var owner = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        storefrontRepo.findByIdAndUserId(storefrontId, owner.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your storefront"));
    }

    private ProductResponse toResponse(StoreProduct p) {
        return new ProductResponse(
                p.getId(), p.getStorefrontId(), p.getName(), p.getDescription(),
                p.getPrice(), p.getStock(), p.isDelisted(),
                readJsonList(p.getMediaUrls()), p.getCategory(), p.isPopular(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }

    private String writeJson(List<String> urls) {
        if (urls == null || urls.isEmpty()) return "[]";
        try {
            return objectMapper.writeValueAsString(urls);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> readJsonList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, List.class);
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
