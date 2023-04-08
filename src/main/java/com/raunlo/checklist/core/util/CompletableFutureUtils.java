package com.raunlo.checklist.core.util;

import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.core.entity.internal.RollbackAction;
import io.vavr.Tuple;
import io.vavr.control.Either;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompletableFutureUtils {

  public static <T> CompletionStage<T> flatMapCompletableFuture(List<CompletionStage<T>> futures,
      BiFunction<T, T, T> mergeFunction) {
    CompletionStage<T> mergedCompletableFuture = null;

    for (CompletionStage<T> future : futures) {
      if (mergedCompletableFuture == null) {
        mergedCompletableFuture = future;
      } else {
        mergedCompletableFuture = mergedCompletableFuture.thenCombine(future, mergeFunction);
      }
    }
    return mergedCompletableFuture;
  }

  public static <T> CompletionStage<Either<Errors, T>> handleResponse(
      CompletionStage<Either<Errors, T>> future,
      RollbackAction<T> errorHandler) {
    return future
        .handle(Tuple::of)
        .thenCompose(operationResponse -> {
          final var throwable = operationResponse._2();
          if (operationResponse._2() != null) {
            return errorHandler.rollback(operationResponse._1())
                .thenApply(__ -> Either.left(Errors.genericError(throwable)));
          }

          return CompletableFuture.completedFuture(operationResponse._1);
        });
  }
}
