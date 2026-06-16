package scancodes.backend.business.Controller;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import scancodes.backend.business.Dtos.BusinessCreateRequest;
import scancodes.backend.business.Dtos.BusinessStorefrontResponse;
import scancodes.backend.business.Repository.BusinessFormRepo;
import scancodes.backend.business.Services.BusinessCreateFormService;
import scancodes.backend.userauth.Repository.UserRepository;

@RestController
public class TemplateFormController {

    private final BusinessCreateFormService businessCreateFormService;
    private final BusinessFormRepo businessFormRepo;
    private final UserRepository userRepository;

    public TemplateFormController(BusinessCreateFormService businessCreateFormService,
                                  BusinessFormRepo businessFormRepo,
                                  UserRepository userRepository) {
        this.businessCreateFormService = businessCreateFormService;
        this.businessFormRepo = businessFormRepo;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/business/storefronts")
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessStorefrontResponse createStorefront(
        @Valid @RequestBody BusinessCreateRequest request,
        Principal principal
    ) {
        return businessCreateFormService.createStorefront(request, principal.getName());
    }

    @GetMapping("/api/business/storefronts/my")
    public List<BusinessStorefrontResponse> getMyStorefronts(Principal principal) {
        var owner = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return businessFormRepo.findAllByUserId(owner.getId())
                .stream()
                .map(businessCreateFormService::toPublicResponse)
                .toList();
    }

    @GetMapping("/api/business/storefronts/{slug}")
    public BusinessStorefrontResponse getStorefrontByApi(@PathVariable String slug) {
        return businessCreateFormService.getPublicStorefront(slug);
    }

    @GetMapping(value = "/api/business/storefronts/{id}/qr-code", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> viewStorefrontQrCode(
        @PathVariable Long id,
        @RequestParam(defaultValue = "512") int size,
        Principal principal
    ) {
        var qrCode = businessCreateFormService.getOwnedStorefrontQrPng(id, principal.getName(), size);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(qrCode);
    }

    @GetMapping(value = "/api/business/storefronts/{id}/qr-code/download", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> downloadStorefrontQrCode(
        @PathVariable Long id,
        @RequestParam(defaultValue = "1024") int size,
        Principal principal
    ) {
        var qrCode = businessCreateFormService.getOwnedStorefrontQrPng(id, principal.getName(), size);
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .header("Content-Disposition", ContentDisposition.attachment()
                .filename("scancode-storefront-" + id + ".png")
                .build()
                .toString())
            .body(qrCode);
    }

    @GetMapping("/{slug:[a-z0-9][a-z0-9-]{0,62}}")
    public BusinessStorefrontResponse getStorefrontByPublicSlug(@PathVariable String slug) {
        return businessCreateFormService.getPublicStorefront(slug);
    }
}
