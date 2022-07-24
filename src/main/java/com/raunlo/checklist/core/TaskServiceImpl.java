package com.raunlo.checklist.core;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.repository.TaskRepository;
import com.raunlo.checklist.core.service.TaskService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Inject
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public CompletionStage<Task> save(final Long checklistId, final Task entity) {
        return taskRepository.save(checklistId, entity);
    }

    @Override
    public CompletionStage<Task> update(final Long checklistId, final Task entity) {
        return taskRepository.update(checklistId, entity);
    }

    @Override
    public CompletionStage<Void> delete(final Long checklistId, final long id) {
        return taskRepository.delete(checklistId, id);
    }

    @Override
    public CompletionStage<Optional<Task>> findById(final Long checklistId, final long id) {
        return taskRepository.findById(checklistId, id);
    }

    @Override
    public CompletionStage<Collection<Task>> getAll(final Long checklistId) {
        return taskRepository.getAll(checklistId);
    }

    @Override
    public CompletionStage<Void> changeOrder(ChangeOrderRequest changeOrderRequest) {
        return taskRepository.changeOrder(changeOrderRequest);
    }
}
