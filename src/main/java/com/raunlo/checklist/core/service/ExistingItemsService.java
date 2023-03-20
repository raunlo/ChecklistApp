package com.raunlo.checklist.core.service;

import com.raunlo.checklist.core.entity.BaseItem;
import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.ExistingItem;
import com.raunlo.checklist.core.entity.error.Errors;
import io.vavr.control.Either;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ExistingItemsService {

  Either<CompletionStage<Errors>, CompletionStage<ExistingItem>> save(final Long checklistId,
    final BaseItem entity);

  Either<CompletionStage<Errors>, CompletionStage<ExistingItem>> update(final Long checklistId,
    final BaseItem entity);

  Either<CompletionStage<Errors>, CompletionStage<Void>> delete(final Long checklistId,
    final long id);

  Either<CompletionStage<Errors>, CompletionStage<Optional<ExistingItem>>> findById(
    final Long checklistId, final long id);

  Either<CompletionStage<Errors>, CompletionStage<Collection<ExistingItem>>> getAll(
    final Long checklistId);

  Either<CompletionStage<Errors>, CompletionStage<Collection<ExistingItem>>> saveAll(
    List<ExistingItem> baseItems, Long checklistId);

  Either<CompletionStage<Errors>, CompletionStage<Void>> changeOrder(
    ChangeOrderRequest changeOrderRequest);
}
