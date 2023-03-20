package com.raunlo.checklist.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class ExceptionMapper implements jakarta.ws.rs.ext.ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        return Response.status(500).entity(e.getMessage()).build();
    }
}
