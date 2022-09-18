package com.raunlo.checklist.core.repository;

import com.raunlo.checklist.core.entity.Task;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface TaskRepository {

    CompletionStage<Task> save(final Long checklistId, final Task entity);

    CompletionStage<Task> update(final Long checklistId, final Task entity);

    CompletionStage<Void> delete(final Long checklistId, final long id);

    CompletionStage<Optional<Task>> findById(final Long checklistId, final long id);

    CompletionStage<Collection<Task>> getAll(final Long checklistId);

    CompletionStage<Void> changeOrder(final List<Task> tasks);

    CompletionStage<List<Task>> findAllTasksInOrderBounds(final long checklistId, final long taskOrderNumber, final Long newOrderNumber);

    CompletionStage<Collection<Task>> saveAll(final List<Task> tasks, Long checklistId);
}
