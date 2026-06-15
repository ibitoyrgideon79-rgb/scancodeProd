package scancodes.backend.business.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import scancodes.backend.business.Dtos.OrderCreateRequest;
import scancodes.backend.business.Dtos.OrderResponse;
import scancodes.backend.business.Services.StoreOrderService;

@RestController
public class StoreOrderController {

    private final StoreOrderService orderService;

    public StoreOrderController(StoreOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/api/storefronts/{storefrontId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @PathVariable Long storefrontId,
            @Valid @RequestBody OrderCreateRequest request) {
        return orderService.createOrder(storefrontId, request);
    }

    @GetMapping("/api/storefronts/{storefrontId}/orders")
    public List<OrderResponse> getOrders(
            @PathVariable Long storefrontId,
            Principal principal) {
        return orderService.getOrdersForStorefront(storefrontId, principal.getName());
    }

    @GetMapping("/api/orders/{orderId}")
    public OrderResponse getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @PatchMapping("/api/storefronts/{storefrontId}/orders/{orderId}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long storefrontId,
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body,
            Principal principal) {
        return orderService.updateOrderStatus(storefrontId, orderId, body.get("status"), principal.getName());
    }

    @PatchMapping("/api/orders/{orderId}/status")
    public OrderResponse updateOrderStatusPublic(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        return orderService.updateOrderStatusPublic(orderId, body.get("status"));
    }
}
