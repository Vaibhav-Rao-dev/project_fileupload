package com.enterprise.storage.presentation.api.exception;

import com.enterprise.storage.presentation.api.dto.ProblemDetailResponse;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {
    
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(NotAuthorizedException exception) {
        ProblemDetailResponse problem = new ProblemDetailResponse(
            "https://enterprise.com/errors/unauthorized",
            "Authentication Failed",
            Response.Status.UNAUTHORIZED.getStatusCode(),
            exception.getMessage(),
            uriInfo.getPath()
        );

        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity(problem)
                .build();
    }
}
