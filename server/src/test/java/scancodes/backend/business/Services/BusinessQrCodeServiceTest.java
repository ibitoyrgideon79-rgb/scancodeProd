package scancodes.backend.business.Services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class BusinessQrCodeServiceTest {

    private final BusinessQrCodeService service = new BusinessQrCodeService();

    @Test
    void generatePngReturnsReadableQrImage() throws Exception {
        var png = service.generatePng("https://scancodes.net/island-lounge", 512);
        var image = ImageIO.read(new ByteArrayInputStream(png));

        assertThat(png).isNotEmpty();
        assertThat(image).isNotNull();
        assertThat(image.getWidth()).isEqualTo(512);
        assertThat(image.getHeight()).isEqualTo(512);
    }

    @Test
    void generatePngClampsTinyRequestedSize() throws Exception {
        var png = service.generatePng("https://scancodes.net/island-lounge", 10);
        var image = ImageIO.read(new ByteArrayInputStream(png));

        assertThat(image.getWidth()).isEqualTo(128);
        assertThat(image.getHeight()).isEqualTo(128);
    }
}
