package com.raunlo.checklist.resource;

import java.util.Optional;
import javax.ws.rs.core.Response;

public interface BaseResource {


    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default <T> Response createResponse(final Optional<T> entityOptional) {
        return entityOptional.map(Response::ok).orElse(Response.status(404))
                .build();
    }
}
