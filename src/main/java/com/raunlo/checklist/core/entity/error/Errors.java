package com.raunlo.checklist.core.entity.error;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.Set;

@RecordBuilder
public record Errors(ErrorType errorType, Set<Error> errors) {

  public static Errors notFoundError(String message) {
    return ErrorsBuilder.builder()
        .errorType(ErrorType.NOT_FOUND_ERROR)
        .errors(Set.of(ErrorBuilder.builder().errorMessage(message).build()))
        .build();
  }

  public static Errors genericError(Throwable throwable) {
    return ErrorsBuilder.builder()
        .errorType(ErrorType.INTERNAL_SERVER_ERROR)
        .errors(Set.of(ErrorBuilder.builder().errorMessage(throwable.getMessage()).build()))
        .build();
  }

}
