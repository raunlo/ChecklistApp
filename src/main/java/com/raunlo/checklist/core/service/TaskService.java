package com.raunlo.checklist.core.service;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.Error;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.entity.Task;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface TaskService {
    Either<CompletionStage<Error>, CompletionStage<Task>> save(final Long checklistId, final Task entity);

    CompletionStage<Task> update(final Long checklistId, final Task entity);

    CompletionStage<Void> delete(final Long checklistId, final long id);

    CompletionStage<Optional<Task>> findById(final Long checklistId, final long id);

    CompletionStage<Collection<Task>> getAll(final Long checklistId, final TaskPredefinedFilter predefineFilter);

    CompletionStage<Collection<Task>> saveAll(List<Task> tasks, Long checklistId);

    CompletionStage<Void> changeOrder(ChangeOrderRequest changeOrderRequest);
}
