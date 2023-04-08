package com.raunlo.checklist.resource;

import com.raunlo.checklist.core.entity.error.ErrorType;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.resource.dto.Identifier;
import com.raunlo.checklist.resource.dto.error.ClientErrorDtoBuilder;
import com.raunlo.checklist.resource.dto.error.ClientErrorsDto;
import com.raunlo.checklist.resource.dto.error.ClientErrorsDtoBuilder;
import io.vavr.control.Either;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseResource {

  private static final Map<ErrorType, Integer> ERROR_STATUS_CODE_MAPPING = Map.of(
      ErrorType.VALIDATION_ERROR, 400,
      ErrorType.NOT_FOUND_ERROR, 404
  );

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  protected <T> Response ok(final Optional<T> entityOptional) {

    return entityOptional.map(Response::ok).orElse(Response.status(404))
        .build();
  }

  protected Response buildErrorResponse(Errors errors) {

    final ClientErrorsDto errorDto = getError(errors);
    return Response.status(ERROR_STATUS_CODE_MAPPING.get(errors.errorType())).entity(errorDto)
        .build();
  }

  protected Response noContent() {

    return Response.noContent().build();
  }

  protected <T extends Identifier> Response created(UriInfo uriInfo, T entity) {

    final URI getResourceURI = uriInfo.getAbsolutePathBuilder()
        .path(String.valueOf(entity.id())).build();
    return Response.created(getResourceURI).entity(entity).build();

  }

  protected Response ok(Object entity) {
    return Response.ok(entity).build();
  }

  protected <T> CompletionStage<Response> mapResponse(
      CompletionStage<Either<Errors, T>> responseFuture,
      Function<T, Response> successfulResponseMapper) {

    return responseFuture.thenApply(resp ->
        resp.map(successfulResponseMapper).getOrElseGet(this::buildErrorResponse));
  }


  private ClientErrorsDto getError(Errors errors) {
    return ClientErrorsDtoBuilder.builder().errors(
            errors.errors().stream()
                .map(error -> ClientErrorDtoBuilder.builder()
                    .fieldName(error.field())
                    .reason(error.errorMessage())
                    .build())
                .collect(Collectors.toSet())
        )
        .build();
  }
}
