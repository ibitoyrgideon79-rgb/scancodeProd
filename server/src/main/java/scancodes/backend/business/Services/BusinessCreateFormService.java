package scancodes.backend.business.Services;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import scancodes.backend.config.AppAuthProperties;
import scancodes.backend.business.Dtos.BusinessCreateRequest;
import scancodes.backend.business.Dtos.BusinessStorefrontResponse;
import scancodes.backend.business.Entities.BusinessFormEntities;
import scancodes.backend.business.Repository.BusinessFormRepo;
import scancodes.backend.userauth.Repository.UserRepository;

@Service
public class BusinessCreateFormService {

    private static final Pattern NOT_SLUG_CHARACTERS = Pattern.compile("[^a-z0-9]+");
    private static final Pattern EDGE_DASHES = Pattern.compile("^-+|-+$");
    private static final int MAX_SLUG_LENGTH = 63;
    private static final Set<String> RESERVED_SLUGS = Set.of(
        "admin", "api", "auth", "css", "dashboard", "error", "js", "login", "logout", "settings"
    );

    private final BusinessFormRepo businessFormRepo;
    private final UserRepository userRepository;
    private final AppAuthProperties appAuthProperties;
    private final BusinessQrCodeService businessQrCodeService;
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    public BusinessCreateFormService(
        BusinessFormRepo businessFormRepo,
        UserRepository userRepository,
        AppAuthProperties appAuthProperties,
        BusinessQrCodeService businessQrCodeService
    ) {
        this.businessFormRepo = businessFormRepo;
        this.userRepository = userRepository;
        this.appAuthProperties = appAuthProperties;
        this.businessQrCodeService = businessQrCodeService;
    }

    @Transactional
    public BusinessStorefrontResponse createStorefront(BusinessCreateRequest request, String username) {
        var owner = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Logged-in user was not found"));

        var storefront = new BusinessFormEntities();
        storefront.setUserId(owner.getId());
        storefront.setBusinessType(request.businessType());
        storefront.setSlug(createUniqueSlug(request.name()));
        storefront.setName(request.name().trim());
        storefront.setDescription(trimToNull(request.description()));
        storefront.setLogoUrl(trimToNull(request.logoUrl()));
        storefront.setBannerUrl(trimToNull(request.bannerUrl()));
        storefront.setData(writeJson(request.data()));
        storefront.setActive(true);

        return toResponse(businessFormRepo.save(storefront));
    }

    @Transactional(readOnly = true)
    public BusinessStorefrontResponse getPublicStorefront(String slug) {
        return businessFormRepo.findBySlugAndIsActiveTrue(slug)
            .map(this::toResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Storefront not found"));
    }

    @Transactional(readOnly = true)
    public byte[] getOwnedStorefrontQrPng(Long storefrontId, String username, int size) {
        var owner = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Logged-in user was not found"));

        var storefront = businessFormRepo.findByIdAndUserId(storefrontId, owner.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Storefront not found for this user"));

        return businessQrCodeService.generatePng(buildPublicUrl(storefront.getSlug()), size);
    }

    private String createUniqueSlug(String businessName) {
        var baseSlug = toSlug(businessName);
        var candidate = baseSlug;
        var suffix = 2;

        while (RESERVED_SLUGS.contains(candidate) || businessFormRepo.existsBySlug(candidate)) {
            var suffixText = "-" + suffix;
            var trimmedBase = trimSlugToMaxLength(baseSlug, MAX_SLUG_LENGTH - suffixText.length());
            candidate = trimmedBase + suffixText;
            suffix++;
        }

        return candidate;
    }

    private String toSlug(String value) {
        var normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT);
        var slug = NOT_SLUG_CHARACTERS.matcher(normalized).replaceAll("-");
        slug = EDGE_DASHES.matcher(slug).replaceAll("");

        if (slug.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business name must contain letters or numbers");
        }

        return trimSlugToMaxLength(slug, MAX_SLUG_LENGTH);
    }

    private String trimSlugToMaxLength(String slug, int maxLength) {
        var trimmed = slug.length() <= maxLength ? slug : slug.substring(0, maxLength);
        return EDGE_DASHES.matcher(trimmed).replaceAll("");
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String writeJson(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Storefront template data must be valid JSON", exception);
        }
    }

    private Map<String, Object> readJson(String data) {
        try {
            return objectMapper.readValue(data, new TypeReference<>() {});
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Stored storefront data is invalid", exception);
        }
    }

    public BusinessStorefrontResponse toPublicResponse(BusinessFormEntities storefront) {
        return toResponse(storefront);
    }

    private BusinessStorefrontResponse toResponse(BusinessFormEntities storefront) {
        return new BusinessStorefrontResponse(
            storefront.getId(),
            storefront.getUserId(),
            storefront.getBusinessType(),
            storefront.getSlug(),
            buildPublicUrl(storefront.getSlug()),
            storefront.getName(),
            storefront.getDescription(),
            storefront.getLogoUrl(),
            storefront.getBannerUrl(),
            readJson(storefront.getData()),
            storefront.isActive(),
            storefront.getCreatedAt(),
            storefront.getUpdatedAt()
        );
    }

    private String buildPublicUrl(String slug) {
        return appAuthProperties.buildUrl("/" + slug);
    }

}
