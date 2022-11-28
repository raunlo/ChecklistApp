package com.raunlo.checklist.resource;

import com.raunlo.checklist.core.entity.Error;
import com.raunlo.checklist.resource.dto.ErrorDto;
import com.raunlo.checklist.resource.dto.ErrorDtoBuilder;
import com.raunlo.checklist.resource.dto.Identifier;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.Optional;

public interface BaseResource {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default <T> Response createResponse(final Optional<T> entityOptional) {
        return entityOptional.map(Response::ok).orElse(Response.status(404))
                .build();
    }

    default <T extends Identifier> Response created(T entity, UriInfo uriInfo) {
        final URI getResourceURI = uriInfo.getAbsolutePathBuilder().path(String.valueOf(entity.id())).build();
        return Response.created(getResourceURI).entity(entity).build();
    }

    default Response buildErrorResponse(Error error) {
        final ErrorDto errorDto = getError(error);
        return Response.status(errorDto.errorCode()).entity(errorDto).build();
    }


    private ErrorDto getError(Error error) {
        return ErrorDtoBuilder.builder()
                .errorCode(Response.Status.BAD_REQUEST.getStatusCode())
                .reason(error.errorMessage())
                .build();
    }
}
