package com.enterprise.storage.presentation.api.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Path("/v1/auth")
public class AuthResource {
    
    // In production, this would be injected via HashiCorp Vault or Environment Variables
    public static final String SECRET_KEY = "EnterpriseSuperSecretKeyThatMustBeAtLeast32BytesLong!";
    
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate() {
        // Issue a token valid for exactly 15 minutes
        String token = JWT.create()
                .withIssuer("enterprise-identity-provider")
                .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256(SECRET_KEY));
                
        return Response.ok(Map.of("token", token)).build();
    }
}
