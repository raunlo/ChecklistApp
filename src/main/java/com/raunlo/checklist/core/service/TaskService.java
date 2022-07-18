package com.raunlo.checklist.core.service;

import com.raunlo.checklist.core.entity.Task;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface TaskService {
    CompletionStage<Task> save(final Long checklistId, final Task entity);

    CompletionStage<Task> update(final Long checklistId, final Task entity);

    CompletionStage<Void> delete(final Long checklistId, final long id);

    CompletionStage<Optional<Task>> findById(final Long checklistId, final long id);

    CompletionStage<Collection<Task>> getAll(final Long checklistId);
}
