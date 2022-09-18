package com.raunlo.checklist.resource;

import jakarta.ws.rs.core.Response;

import java.util.Optional;

public interface BaseResource {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default <T> Response createResponse(final Optional<T> entityOptional) {
        return entityOptional.map(Response::ok).orElse(Response.status(404))
                .build();
    }
}
