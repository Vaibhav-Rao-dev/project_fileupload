package com.enterprise.storage.presentation.api.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.Priority;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION) 
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private final JWTVerifier verifier = JWT.require(Algorithm.HMAC256(AuthResource.SECRET_KEY))
            .withIssuer("enterprise-identity-provider")
            .build();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        
        // FIX: JAX-RS strips the leading slash. Using endsWith makes this framework-resilient.
        if (path.endsWith("v1/auth/login")) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Missing or malformed Authorization header.");
        }

        try {
            String token = authHeader.substring(7); // Strip "Bearer " prefix
            verifier.verify(token); // Cryptographically validate the signature and expiration
        } catch (Exception e) {
            throw new NotAuthorizedException("Invalid or expired JWT token.");
        }
    }
}
