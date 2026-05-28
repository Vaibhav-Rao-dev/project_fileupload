package com.enterprise.storage.bootstrap.config;

import com.enterprise.storage.presentation.api.FileStorageResource;
import com.enterprise.storage.presentation.api.auth.AuthResource;
import com.enterprise.storage.presentation.api.auth.JwtAuthenticationFilter;
import com.enterprise.storage.presentation.api.exception.GlobalExceptionMapper;
import com.enterprise.storage.presentation.api.exception.IllegalArgumentExceptionMapper;
import com.enterprise.storage.presentation.api.exception.NotAuthorizedExceptionMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // Endpoints
        register(FileStorageResource.class);
        register(AuthResource.class);
        
        // Security Filters
        register(JwtAuthenticationFilter.class);
        
        // Exception Mappers
        register(NotAuthorizedExceptionMapper.class);
        register(IllegalArgumentExceptionMapper.class);
        register(GlobalExceptionMapper.class);
    }
}
