package scancodes.backend.business.Controller;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import scancodes.backend.business.Dtos.ProductCreateRequest;
import scancodes.backend.business.Dtos.ProductResponse;
import scancodes.backend.business.Services.StoreProductService;

@RestController
@RequestMapping("/api/storefronts/{storefrontId}/products")
public class StoreProductController {

    private final StoreProductService productService;

    public StoreProductController(StoreProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @PathVariable Long storefrontId,
            @Valid @RequestBody ProductCreateRequest request,
            Principal principal) {
        return productService.createProduct(storefrontId, request, principal.getName());
    }

    @GetMapping
    public List<ProductResponse> getPublicProducts(@PathVariable Long storefrontId) {
        return productService.getPublicProducts(storefrontId);
    }

    @GetMapping("/all")
    public List<ProductResponse> getAllProducts(
            @PathVariable Long storefrontId,
            Principal principal) {
        return productService.getAllProducts(storefrontId, principal.getName());
    }

    @PatchMapping("/{productId}/toggle-delist")
    public ProductResponse toggleDelist(
            @PathVariable Long storefrontId,
            @PathVariable Long productId,
            Principal principal) {
        return productService.toggleDelist(storefrontId, productId, principal.getName());
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @PathVariable Long storefrontId,
            @PathVariable Long productId,
            Principal principal) {
        productService.deleteProduct(storefrontId, productId, principal.getName());
    }
}
