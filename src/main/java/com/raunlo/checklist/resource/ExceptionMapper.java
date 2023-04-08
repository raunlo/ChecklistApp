package com.raunlo.checklist.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public final class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        return Response.status(500).entity(e.getMessage()).build();
    }
}
