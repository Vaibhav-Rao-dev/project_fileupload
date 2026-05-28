package com.enterprise.storage.presentation.api.exception;

import com.enterprise.storage.presentation.api.dto.ProblemDetailResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        ProblemDetailResponse problem = new ProblemDetailResponse(
            "https://enterprise.com/errors/bad-request",
            "Invalid Request Parameters",
            Response.Status.BAD_REQUEST.getStatusCode(),
            exception.getMessage(), // Safely pass domain validation messages to the client
            uriInfo.getPath()
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(problem)
                .build();
    }
}
