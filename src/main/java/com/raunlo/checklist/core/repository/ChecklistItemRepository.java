package com.raunlo.checklist.core.repository;

import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ChecklistItemRepository {

  CompletionStage<ChecklistItem> save(final Long checklistId, final ChecklistItem entity);

  CompletionStage<ChecklistItem> update(final Long checklistId, final ChecklistItem entity);

  CompletionStage<Void> delete(final long checklistId, final long id);

  CompletionStage<Optional<ChecklistItem>> findById(final long checklistId, final long id);

  CompletionStage<Collection<ChecklistItem>> getAll(final long checklistId,
      final TaskPredefinedFilter predefinedFilter);

  CompletionStage<Void> changeOrder(final List<ChecklistItem> items);

  CompletionStage<List<ChecklistItem>> findAllTasksInOrderBounds(final long checklistId,
      final int taskOrderNumber, final int newOrderNumber);

  CompletionStage<Collection<ChecklistItem>> saveAll(long checklistId,
      final List<ChecklistItem> items);
}