package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.repository.TaskRepository;
import com.raunlo.checklist.persistence.mapper.TaskMapper;
import com.raunlo.checklist.persistence.model.TaskDbo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
class TaskDelegateRepository implements TaskRepository {

    private final TaskDao taskDao;
    private final TaskMapper taskMapper;

    @Inject()
    TaskDelegateRepository(TaskDao taskDao, TaskMapper taskMapper) {
        this.taskDao = taskDao;
        this.taskMapper = taskMapper;
    }

    @Override
    public CompletionStage<Task> save(final Long checklistId, final Task entity) {
        return CompletableFuture.supplyAsync(() ->
                taskMapper.map(taskDao.insert(entity.getName(), entity.isCompleted(), checklistId)));
    }

    @Override
    public CompletionStage<Task> update(final Long checklistId, final Task entity) {
        return CompletableFuture.supplyAsync(() -> {
            taskDao.updateTask(checklistId, entity.getId(), entity.isCompleted(), entity.getName());
            return entity;
        });
    }

    @Override
    public CompletionStage<Void> delete(final Long checklistId, final long id) {
        return CompletableFuture.runAsync(() ->
                taskDao.deleteById(checklistId, id));
    }

    @Override
    public CompletionStage<Optional<Task>> findById(final Long checklistId, final long id) {
        return CompletableFuture.supplyAsync(() ->
                taskDao.findById(checklistId, id)
                        .map(taskMapper::map));
    }

    @Override
    public CompletionStage<Collection<Task>> getAll(final Long checklistId) {
        return CompletableFuture.supplyAsync(() ->
                taskDao.getAllTasks(checklistId)
                        .stream()
                        .map(taskMapper::map)
                        .collect(toList()));
    }

    @Override
    public CompletionStage<Void> changeOrder(List<Task> tasks) {
        return CompletableFuture.runAsync(() -> {
            final List<TaskDbo> taskDbos = tasks
                    .stream()
                    .map(taskMapper::map)
                    .collect(toList());
            taskDao.updateTasksOrder(taskDbos);
        });
    }

    @Override
    public CompletionStage<List<Task>> findAllTasksInOrderBounds(long lowerBound, long upperBound) {
        return CompletableFuture.supplyAsync(() ->
                taskDao.findTasksInOrderBounds(lowerBound, upperBound)
                        .stream()
                        .map(taskMapper::map)
                        .collect(toList()));
    }
}
