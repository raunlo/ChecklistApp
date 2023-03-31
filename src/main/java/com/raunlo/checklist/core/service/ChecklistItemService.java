package com.raunlo.checklist.core.service;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.entity.error.Errors;
import io.vavr.control.Either;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ChecklistItemService {

  CompletionStage<Either<Errors, ChecklistItem>> save(final Long checklistId,
    final ChecklistItem entity);

  CompletionStage<Either<Errors, ChecklistItem>> update(final Long checklistId,
    final ChecklistItem entity);

  CompletionStage<Either<Errors, Void>> delete(final Long checklistId,
    final Long id);

  CompletionStage<Either<Errors, Optional<ChecklistItem>>> findById(
    final Long checklistId, final Long id);

  CompletionStage<Either<Errors, Collection<ChecklistItem>>> getAll(
    final Long checklistId, final TaskPredefinedFilter predefineFilter);

  CompletionStage<Either<Errors, Collection<ChecklistItem>>> saveAll(
    List<ChecklistItem> baseItems, Long checklistId);

  CompletionStage<Either<Errors, Void>> changeOrder(ChangeOrderRequest changeOrderRequest);
}
