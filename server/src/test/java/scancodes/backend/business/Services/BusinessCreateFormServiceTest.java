package scancodes.backend.business.Services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import scancodes.backend.business.Dtos.BusinessCreateRequest;
import scancodes.backend.business.Entities.BusinessFormEntities;
import scancodes.backend.business.Entities.BusinessType;
import scancodes.backend.business.Repository.BusinessFormRepo;
import scancodes.backend.config.AppAuthProperties;
import scancodes.backend.userauth.Entity.AppUserEntity;
import scancodes.backend.userauth.Repository.UserRepository;

class BusinessCreateFormServiceTest {

    private BusinessFormRepo businessFormRepo;
    private UserRepository userRepository;
    private BusinessQrCodeService businessQrCodeService;
    private BusinessCreateFormService service;

    @BeforeEach
    void setUp() {
        businessFormRepo = mock(BusinessFormRepo.class);
        userRepository = mock(UserRepository.class);
        businessQrCodeService = mock(BusinessQrCodeService.class);

        var properties = new AppAuthProperties();
        properties.setPublicBaseUrl("https://scancodes.net");

        service = new BusinessCreateFormService(
            businessFormRepo,
            userRepository,
            properties,
            businessQrCodeService
        );
    }

    @Test
    void createStorefrontUsesLoggedInUserAndReturnsPublicUrl() {
        var user = new AppUserEntity();
        user.setId(7L);
        user.setUsername("ada");
        when(userRepository.findByUsername("ada")).thenReturn(Optional.of(user));
        when(businessFormRepo.save(any(BusinessFormEntities.class))).thenAnswer(invocation -> {
            BusinessFormEntities storefront = invocation.getArgument(0);
            storefront.setId(10L);
            return storefront;
        });

        var request = new BusinessCreateRequest(
            BusinessType.RESTAURANT,
            "Island Lounge",
            "Modern lounge",
            null,
            null,
            Map.of("phone", "+2348012345678")
        );

        var response = service.createStorefront(request, "ada");

        assertThat(response.userId()).isEqualTo(7L);
        assertThat(response.slug()).isEqualTo("island-lounge");
        assertThat(response.publicUrl()).isEqualTo("https://scancodes.net/island-lounge");
        assertThat(response.data()).containsEntry("phone", "+2348012345678");
    }

    @Test
    void createStorefrontAddsSuffixWhenSlugAlreadyExists() {
        var user = new AppUserEntity();
        user.setId(7L);
        when(userRepository.findByUsername("ada")).thenReturn(Optional.of(user));
        when(businessFormRepo.existsBySlug("island-lounge")).thenReturn(true);
        when(businessFormRepo.save(any(BusinessFormEntities.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new BusinessCreateRequest(
            BusinessType.PRODUCT,
            "Island Lounge",
            null,
            null,
            null,
            Map.of("items", 3)
        );

        var response = service.createStorefront(request, "ada");

        assertThat(response.slug()).isEqualTo("island-lounge-2");
    }

    @Test
    void getOwnedStorefrontQrPngOnlyAllowsOwner() {
        var user = new AppUserEntity();
        user.setId(7L);
        user.setUsername("ada");
        var storefront = new BusinessFormEntities();
        storefront.setId(10L);
        storefront.setUserId(7L);
        storefront.setSlug("island-lounge");
        when(userRepository.findByUsername("ada")).thenReturn(Optional.of(user));
        when(businessFormRepo.findByIdAndUserId(10L, 7L)).thenReturn(Optional.of(storefront));
        when(businessQrCodeService.generatePng("https://scancodes.net/island-lounge", 512))
            .thenReturn(new byte[] {1, 2, 3});

        var qrCode = service.getOwnedStorefrontQrPng(10L, "ada", 512);

        assertThat(qrCode).containsExactly(1, 2, 3);
    }

    @Test
    void getOwnedStorefrontQrPngReturnsNotFoundWhenUserDoesNotOwnStorefront() {
        var user = new AppUserEntity();
        user.setId(7L);
        when(userRepository.findByUsername("ada")).thenReturn(Optional.of(user));
        when(businessFormRepo.findByIdAndUserId(10L, 7L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.getOwnedStorefrontQrPng(10L, "ada", 512));
        verify(businessQrCodeService, never()).generatePng(any(), eq(512));
    }
}
