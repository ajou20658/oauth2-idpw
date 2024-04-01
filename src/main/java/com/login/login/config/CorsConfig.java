package com.login.login.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

public class CorsConfig implements CorsConfigurationSource {
    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        return config;
    }
}
