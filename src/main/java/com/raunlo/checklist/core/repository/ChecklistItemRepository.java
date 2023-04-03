package com.raunlo.checklist.core.repository;

import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChecklistItemRepository {

  ChecklistItem save(final Long checklistId, final ChecklistItem entity);

  ChecklistItem update(final Long checklistId, final ChecklistItem entity);

  void delete(final long checklistId, final long id);

  Optional<ChecklistItem> findById(final long checklistId, final long id);

  Collection<ChecklistItem> getAll(final long checklistId,
      final TaskPredefinedFilter predefinedFilter);

  void changeOrder(final List<ChecklistItem> items);

  List<ChecklistItem> findAllTasksInOrderBounds(final long checklistId,
      final int taskOrderNumber, final int newOrderNumber);

  Collection<ChecklistItem> saveAll(long checklistId,
      final List<ChecklistItem> items);

  void removeTaskFromOrderLink(long checklistId, long taskId);

  void updateSavedItemOrderLink(long checklistId, long taskId);
}
