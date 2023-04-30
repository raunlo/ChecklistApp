package com.raunlo.checklist.core.util;

import java.util.List;
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
}
