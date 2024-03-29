package com.raunlo.checklist.core.util;

import com.raunlo.checklist.core.entity.error.Errors;
import io.vavr.control.Either;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

public final class EitherUtil {

  public static <L, Input, Output> CompletionStage<Either<L, Output>> mapCompletableStage(
      final Either<L, Input> either, final Supplier<CompletionStage<Output>> mapper) {
    if (either.isRight()) {
      return mapper
          .get()
          .thenApply(
              value -> {
                if (value != null && value.getClass().equals(Either.class)) {
                  return either.map(__ -> ((Either<L, Output>) value).get());
                } else {
                  return either.map(__ -> value);
                }
              });
    }
    return CompletableFuture.completedStage((Either<L, Output>) either);
  }

  public static <L, Input, Output> CompletionStage<Either<L, Output>> mapCompletableStage(
      final Either<L, Input> either, final Function<Input, CompletionStage<Output>> mapper) {
    if (either.isRight()) {
      return mapper
          .apply(either.get())
          .thenApply(
              value -> {
                if (value != null && value.getClass().equals(Either.class)) {
                  return either.map(__ -> ((Either<L, Output>) value).get());
                } else {
                  return either.map(__ -> value);
                }
              });
    }
    return CompletableFuture.completedStage((Either<L, Output>) either);
  }


  public static <R>  Either<Errors, R>  mapOptionaToEither(Optional<R> value, String notFoundMessage) {
      return value.map(Either::<Errors, R>right)
          .orElseGet(() -> Either.left(Errors.notFoundError(notFoundMessage)));
  }
}
