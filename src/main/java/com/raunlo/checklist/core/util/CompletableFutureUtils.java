package com.raunlo.checklist.core.util;

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
    var mergedCompletableFuture = new CompletableFuture<T>();

    for (CompletionStage<T> future : futures) {
      mergedCompletableFuture = mergedCompletableFuture.thenCombine(future, mergeFunction::apply);
    }
    return mergedCompletableFuture;
  }
}
