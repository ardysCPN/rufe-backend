package co.rufe.rufe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.local-dir:}")
    private String configuredBaseDir;

    private String getBaseDir() {
        if (configuredBaseDir != null && !configuredBaseDir.isBlank()) {
            return configuredBaseDir;
        }
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        return isWindows ? "c:/rufe/evidences" : "/app/uploads";
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**")
          .allowedOrigins("http://localhost:4200", "http://localhost:4000")
          .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
          .allowedHeaders("*")
          .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String baseDir = getBaseDir().replace("\\", "/");
        if (!baseDir.endsWith("/")) {
            baseDir += "/";
        }
        registry.addResourceHandler("/api/public/evidencias/**")
                .addResourceLocations("file:" + baseDir);
    }
}

