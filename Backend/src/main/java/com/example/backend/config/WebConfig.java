package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        List<String> locations = new ArrayList<>();
        for (Path dir : UploadPaths.candidateDirs()) {
            try {
                Files.createDirectories(dir);
                locations.add(dir.toUri().toString());
            } catch (Exception ignored) {
                // skip unusable path
            }
        }

        if (!locations.isEmpty()) {
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(locations.toArray(String[]::new));
        }
    }
}
