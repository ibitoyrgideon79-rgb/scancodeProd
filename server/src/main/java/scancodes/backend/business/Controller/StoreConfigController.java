package scancodes.backend.business.Controller;

import java.security.Principal;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import scancodes.backend.business.Dtos.StoreConfigRequest;
import scancodes.backend.business.Dtos.StoreConfigResponse;
import scancodes.backend.business.Services.StoreConfigService;

@RestController
@RequestMapping("/api/storefronts/{storefrontId}/config")
public class StoreConfigController {

    private final StoreConfigService configService;

    public StoreConfigController(StoreConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public StoreConfigResponse getConfig(@PathVariable Long storefrontId) {
        return configService.getConfig(storefrontId);
    }

    @PutMapping
    public StoreConfigResponse saveConfig(
            @PathVariable Long storefrontId,
            @Valid @RequestBody StoreConfigRequest request,
            Principal principal) {
        return configService.saveConfig(storefrontId, request, principal.getName());
    }
}
