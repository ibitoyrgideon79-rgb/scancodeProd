package scancodes.backend.business.Controller;

import java.security.Principal;
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

import scancodes.backend.business.Dtos.BusinessCreateRequest;
import scancodes.backend.business.Dtos.BusinessStorefrontResponse;
import scancodes.backend.business.Services.BusinessCreateFormService;

@RestController
public class TemplateFormController {

    private final BusinessCreateFormService businessCreateFormService;

    public TemplateFormController(BusinessCreateFormService businessCreateFormService) {
        this.businessCreateFormService = businessCreateFormService;
    }

    @PostMapping("/api/business/storefronts")
    @ResponseStatus(HttpStatus.CREATED)
    public BusinessStorefrontResponse createStorefront(
        @Valid @RequestBody BusinessCreateRequest request,
        Principal principal
    ) {
        return businessCreateFormService.createStorefront(request, principal.getName());
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
