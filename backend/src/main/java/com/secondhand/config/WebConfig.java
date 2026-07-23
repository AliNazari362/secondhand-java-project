package com.secondhand.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for serving static resources.
 *
 * <p>Configures resource handlers to expose the {@code uploads/} directory
 * so that uploaded images can be accessed via HTTP requests under the
 * {@code /uploads/**} path.</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Adds resource handlers to serve files from the {@code uploads/} directory.
     *
     * <p>Maps the {@code /uploads/**} URL pattern to the file system location
     * {@code file:uploads/}.</p>
     *
     * @param registry the resource handler registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}