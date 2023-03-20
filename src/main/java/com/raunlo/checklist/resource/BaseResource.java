package com.raunlo.checklist.resource;

import com.raunlo.checklist.core.entity.error.ErrorType;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.resource.dto.error.ClientErrorDtoBuilder;
import com.raunlo.checklist.resource.dto.error.ClientErrorsDto;
import com.raunlo.checklist.resource.dto.error.ClientErrorsDtoBuilder;
import com.raunlo.checklist.resource.dto.item.Identifier;
import io.vavr.control.Either;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseResource {

  private static final Map<ErrorType, Integer> ERROR_STATUS_CODE_MAPPING = Map.of(
    ErrorType.VALIDATION_ERROR, 400
  );

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  protected <T> Response createResponse(final Optional<T> entityOptional) {
    return entityOptional.map(Response::ok).orElse(Response.status(404))
      .build();
  }

  protected <T extends Identifier> Response created(T entity, UriInfo uriInfo) {
    final URI getResourceURI = uriInfo.getAbsolutePathBuilder().path(String.valueOf(entity.getId()))
      .build();
    return Response.created(getResourceURI).entity(entity).build();
  }

  protected Response buildErrorResponse(Errors errors) {
    final ClientErrorsDto errorDto = getError(errors);
    return Response.status(ERROR_STATUS_CODE_MAPPING.get(errors.errorType())).entity(errorDto)
      .build();
  }

  protected <T> CompletionStage<Response> mapResponse(
    Either<CompletionStage<Errors>, CompletionStage<T>> response,
    Function<T, Response> successfulResponseMapper) {
    return response.map(responseFuture ->
      responseFuture.thenApply(successfulResponseMapper)
    ).getOrElseGet(errorFuture -> errorFuture.thenApply(this::buildErrorResponse));
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
