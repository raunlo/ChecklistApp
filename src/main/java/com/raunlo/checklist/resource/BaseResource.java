package com.raunlo.checklist.resource;

import java.util.Optional;
import javax.ws.rs.core.Response;

public interface BaseResource {


    default Response createResponse(Optional<?> entity) {
        if (entity.isPresent()) {
            return Response.ok(entity).build();
        } else {
            return Response.status(404).build();
        }
    }
}
