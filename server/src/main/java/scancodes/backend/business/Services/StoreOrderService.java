package scancodes.backend.business.Services;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import scancodes.backend.business.Dtos.OrderCreateRequest;
import scancodes.backend.business.Dtos.OrderResponse;
import scancodes.backend.business.Entities.StoreOrder;
import scancodes.backend.business.Repository.BusinessFormRepo;
import scancodes.backend.business.Repository.StoreOrderRepository;
import scancodes.backend.userauth.Repository.UserRepository;

@Service
public class StoreOrderService {

    private final StoreOrderRepository orderRepo;
    private final BusinessFormRepo storefrontRepo;
    private final UserRepository userRepo;
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    public StoreOrderService(StoreOrderRepository orderRepo,
                             BusinessFormRepo storefrontRepo,
                             UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.storefrontRepo = storefrontRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public OrderResponse createOrder(Long storefrontId, OrderCreateRequest request) {
        // Verify storefront exists
        storefrontRepo.findById(storefrontId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Storefront not found"));

        var order = new StoreOrder();
        order.setStorefrontId(storefrontId);
        order.setCustomerName(request.customerName());
        order.setCustomerPhone(request.customerPhone());
        order.setCustomerEmail(request.customerEmail());
        order.setOrderItems(writeJson(request.items()));
        order.setSubtotal(request.subtotal());
        order.setVat(request.vat());
        order.setDelivery(request.delivery());
        order.setTotal(request.total());
        order.setStatus(StoreOrder.OrderStatus.PENDING);

        return toResponse(orderRepo.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForStorefront(Long storefrontId, String username) {
        verifyOwnership(storefrontId, username);
        return orderRepo.findAllByStorefrontIdOrderByCreatedAtDesc(storefrontId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long storefrontId, Long orderId, String status, String username) {
        verifyOwnership(storefrontId, username);

        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getStorefrontId().equals(storefrontId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order does not belong to this storefront");
        }

        try {
            order.setStatus(StoreOrder.OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }

        return toResponse(orderRepo.save(order));
    }

    @Transactional
    public OrderResponse updateOrderStatusPublic(Long orderId, String status) {
        var order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Customers can only notify
        if (!"CUSTOMER_NOTIFIED".equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customers can only set status to CUSTOMER_NOTIFIED");
        }

        order.setStatus(StoreOrder.OrderStatus.CUSTOMER_NOTIFIED);
        return toResponse(orderRepo.save(order));
    }

    private void verifyOwnership(Long storefrontId, String username) {
        var owner = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        storefrontRepo.findByIdAndUserId(storefrontId, owner.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your storefront"));
    }

    private OrderResponse toResponse(StoreOrder o) {
        return new OrderResponse(
                o.getId(), o.getStorefrontId(),
                o.getCustomerName(), o.getCustomerPhone(), o.getCustomerEmail(),
                o.getOrderItems(), o.getSubtotal(), o.getVat(), o.getDelivery(), o.getTotal(),
                o.getStatus().name(), o.getCreatedAt(), o.getUpdatedAt()
        );
    }

    private String writeJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
