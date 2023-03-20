package com.raunlo.checklist.core.processor;

import static com.raunlo.checklist.core.entity.error.ErrorMessages.CHECKLIST_ITEM_IS_MISSING;
import static com.raunlo.checklist.core.entity.error.ErrorType.NOT_FOUND_ERROR;

import com.raunlo.checklist.core.entity.BaseItem;
import com.raunlo.checklist.core.entity.error.Error;
import com.raunlo.checklist.core.entity.error.ErrorBuilder;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.core.entity.error.ErrorsBuilder;
import com.raunlo.checklist.core.util.EitherUtil;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public record ChangeOrderProcessor<T extends BaseItem>(
  Supplier<CompletionStage<Optional<T>>> getItemOrderNumberSupplier, long newOrderNumber,
  Function<Integer, CompletionStage<List<T>>> findAllItemsInCurrentAndOldOrderNumberBounds,
  Function<List<T>, CompletionStage<Void>> updateOrderFunction) {

  public CompletionStage<Either<Errors, Void>> changeOrder() {
    final var oldTaskOrderNumberEither = getItemOrderNumber();
    return oldTaskOrderNumberEither
      .thenCompose(this::getItemsInOrderBounds)
      .thenApply(this::correctItemsOrder)
      .thenCompose(this::updateItemsOrder);
  }

  private CompletionStage<Either<Errors, Integer>> getItemOrderNumber() {
    return this.getItemOrderNumberSupplier.get().thenApply(
      item -> item.map(checklistItem -> Either.<Errors, Integer>right(checklistItem.getOrder().intValue()))
        .orElseGet(() -> {
          final Error error = ErrorBuilder.builder().errorMessage(CHECKLIST_ITEM_IS_MISSING)
            .build();
          return Either.left(
            ErrorsBuilder.builder()
              .errors(Set.of(error))
              .errorType(NOT_FOUND_ERROR)
              .build()
          );
        }));
  }

  private CompletionStage<Either<Errors, Tuple2<Integer, List<T>>>> getItemsInOrderBounds(
    final Either<Errors, Integer> itemOrderNumberEither) {
    if (itemOrderNumberEither.isLeft()) {
      return CompletableFuture.completedStage(Either.left(itemOrderNumberEither.getLeft()));
    }

    final int itemOrderNumber = itemOrderNumberEither.get();

    final var itemsInOrderBounds = findAllItemsInCurrentAndOldOrderNumberBounds.apply(
        itemOrderNumber)
      .thenApply(itemsInbounds -> Tuple.of(itemOrderNumber, itemsInbounds));

    return EitherUtil.mapCompletableStage(itemOrderNumberEither, () -> itemsInOrderBounds);

  }

  private Either<Errors, List<T>> correctItemsOrder(
    Either<Errors, Tuple2<Integer, List<T>>> itemsWithOldOrderNumberEither) {
    if (itemsWithOldOrderNumberEither.isLeft()) {
      return Either.left(itemsWithOldOrderNumberEither.getLeft());
    }

    final var items = itemsWithOldOrderNumberEither.get()._2();
    final var oldOrderNumber = itemsWithOldOrderNumberEither.get()._1();

    final List<T> result = new ArrayList<>(items.size());
    final boolean taskOrderNumberDecreased = oldOrderNumber < newOrderNumber;

    for (int i = 0; i < items.size(); i++) {
      final BaseItem baseItem = items.get(i);
      if (i == 0 && taskOrderNumberDecreased) {
        result.add((T) baseItem.withOrder(newOrderNumber));
        continue;
      }

      if (i == items.size() - 1 && !taskOrderNumberDecreased) {
        result.add((T) baseItem.withOrder(newOrderNumber));
        continue;
      }
      final long newElementOrder =
        taskOrderNumberDecreased ? baseItem.getOrder() - 1 : baseItem.getOrder() + 1;
      result.add((T) baseItem.withOrder(newElementOrder));
    }

    return Either.right(result);
  }

  private CompletionStage<Either<Errors, Void>> updateItemsOrder(
    Either<Errors, List<T>> itemsInCorrectOrderEither) {
    if (itemsInCorrectOrderEither.isLeft()) {
      return CompletableFuture.completedStage(Either.left(itemsInCorrectOrderEither.getLeft()));
    }

    return updateOrderFunction.apply(itemsInCorrectOrderEither.get())
      .thenApply(Either::right);
  }
}
