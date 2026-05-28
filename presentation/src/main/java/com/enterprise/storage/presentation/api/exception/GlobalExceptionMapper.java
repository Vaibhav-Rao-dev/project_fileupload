package com.enterprise.storage.presentation.api.exception;

import com.enterprise.storage.presentation.api.dto.ProblemDetailResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    
    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {
        // In a production environment, you would inject an SLF4J Logger here 
        // to record the exception.printStackTrace() for your internal telemetry.
        
        ProblemDetailResponse problem = new ProblemDetailResponse(
            "https://enterprise.com/errors/internal-error",
            "Internal Server Error",
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
            "An unexpected system failure occurred. Our engineering team has been notified.",
            uriInfo.getPath()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(problem)
                .build();
    }
}
