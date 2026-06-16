package scancodes.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.auth")
public class AppAuthProperties {
  private String publicBaseUrl = "http://localhost:8082";

  public String getPublicBaseUrl() {
    return publicBaseUrl;
  }

  public void setPublicBaseUrl(String publicBaseUrl) {
    this.publicBaseUrl = publicBaseUrl;
  }

  public String buildUrl(String pathAndQuery) {
    String base = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
    String suffix = pathAndQuery.startsWith("/") ? pathAndQuery : "/" + pathAndQuery;
    return base + suffix;
  }
}
