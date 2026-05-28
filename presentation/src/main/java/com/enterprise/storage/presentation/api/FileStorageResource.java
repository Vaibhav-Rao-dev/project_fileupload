package com.enterprise.storage.presentation.api;

import com.enterprise.storage.domain.model.FileMetadataContext;
import com.enterprise.storage.domain.ports.api.FileStorageUseCase;
import com.enterprise.storage.presentation.api.dto.InitiateUploadRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.UUID;

@Path("/v1/files")
@Produces(MediaType.APPLICATION_JSON)
public class FileStorageResource {

    private final FileStorageUseCase fileStorageUseCase;

    @Inject
    public FileStorageResource(FileStorageUseCase fileStorageUseCase) {
        this.fileStorageUseCase = fileStorageUseCase;
    }

    @POST
    @Path("/initiate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response initiateUpload(InitiateUploadRequest request) {
        FileMetadataContext context = fileStorageUseCase.initiateUpload(
            request.fileName(), request.contentType(), request.sizeBytes()
        );
        return Response.status(Response.Status.CREATED).entity(context).build();
    }

    @PUT
    @Path("/{id}/chunks")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadChunk(
            @PathParam("id") UUID id, 
            InputStream chunkStream, 
            @HeaderParam("Content-Length") long chunkSize) {
            
        fileStorageUseCase.uploadChunk(id, chunkStream, chunkSize);
        return Response.accepted().build();
    }

    @POST
    @Path("/{id}/complete")
    public Response completeUpload(@PathParam("id") UUID id) {
        FileMetadataContext context = fileStorageUseCase.completeUpload(id);
        return Response.ok(context).build();
    }
}
