package scancodes.backend.business.Services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import scancodes.backend.business.Dtos.StoreConfigRequest;
import scancodes.backend.business.Dtos.StoreConfigResponse;
import scancodes.backend.business.Entities.StoreConfig;
import scancodes.backend.business.Repository.BusinessFormRepo;
import scancodes.backend.business.Repository.StoreConfigRepository;
import scancodes.backend.userauth.Repository.UserRepository;

@Service
public class StoreConfigService {

    private final StoreConfigRepository configRepo;
    private final BusinessFormRepo storefrontRepo;
    private final UserRepository userRepo;

    public StoreConfigService(StoreConfigRepository configRepo,
                              BusinessFormRepo storefrontRepo,
                              UserRepository userRepo) {
        this.configRepo = configRepo;
        this.storefrontRepo = storefrontRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public StoreConfigResponse getConfig(Long storefrontId) {
        var config = configRepo.findByStorefrontId(storefrontId)
                .orElse(createDefaultConfig(storefrontId));
        return toResponse(config);
    }

    @Transactional
    public StoreConfigResponse saveConfig(Long storefrontId, StoreConfigRequest request, String username) {
        verifyOwnership(storefrontId, username);

        var config = configRepo.findByStorefrontId(storefrontId)
                .orElseGet(() -> {
                    var c = new StoreConfig();
                    c.setStorefrontId(storefrontId);
                    return c;
                });

        config.setVatRate(request.vatRate() / 100.0); // Frontend sends percentage, store as decimal
        config.setDeliveryFee(request.deliveryFee());

        return toResponse(configRepo.save(config));
    }

    private StoreConfig createDefaultConfig(Long storefrontId) {
        var config = new StoreConfig();
        config.setStorefrontId(storefrontId);
        config.setVatRate(0.075);
        config.setDeliveryFee(2000);
        return config;
    }

    private void verifyOwnership(Long storefrontId, String username) {
        var owner = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        storefrontRepo.findByIdAndUserId(storefrontId, owner.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your storefront"));
    }

    private StoreConfigResponse toResponse(StoreConfig c) {
        return new StoreConfigResponse(c.getId(), c.getStorefrontId(), c.getVatRate(), c.getDeliveryFee());
    }
}
