package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.repository.TaskRepository;
import com.raunlo.checklist.persistence.dao.TaskDao;
import com.raunlo.checklist.persistence.mapper.TaskMapper;
import com.raunlo.checklist.persistence.model.TaskDbo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
class TaskDelegateRepository implements TaskRepository {

    private final TaskDao taskDao;
    private final TaskMapper taskMapper;
    private final ExecutorService executorService;

    @Inject()
    TaskDelegateRepository(TaskDao taskDao, TaskMapper taskMapper) {
        executorService = Executors.newScheduledThreadPool(2);
        this.taskDao = taskDao;
        this.taskMapper = taskMapper;
    }

    @Override
    public CompletionStage<Task> save(final Long checklistId, final Task entity) {
        return CompletableFuture.supplyAsync(() ->
                        taskMapper.map(taskDao.insert(entity.getName(), entity.isCompleted(), checklistId)),
                executorService);
    }

    @Override
    public CompletionStage<Task> update(final Long checklistId, final Task entity) {
        return CompletableFuture.supplyAsync(() -> {
            taskDao.updateTask(checklistId, entity.getId(), entity.isCompleted(), entity.getName());
            return entity;
        }, executorService);
    }

    @Override
    public CompletionStage<Void> delete(final Long checklistId, final long id) {
        return CompletableFuture.runAsync(() ->
                taskDao.deleteById(checklistId, id), executorService);
    }

    @Override
    public CompletionStage<Optional<Task>> findById(final Long checklistId, final long id) {
        return CompletableFuture.supplyAsync(() ->
                taskDao.findById(checklistId, id)
                        .map(taskMapper::map), executorService);
    }

    @Override
    public CompletionStage<Collection<Task>> getAll(final Long checklistId, final TaskPredefinedFilter predefinedFilter) {
        return CompletableFuture.supplyAsync(() ->
                taskDao.getAllTasks(checklistId, predefinedFilter)
                        .stream()
                        .map(taskMapper::map)
                        .toList(), executorService);
    }

    @Override
    public CompletionStage<Void> changeOrder(List<Task> tasks) {
        return CompletableFuture.runAsync(() -> {
            final List<TaskDbo> taskDbos = tasks
                    .stream()
                    .map(taskMapper::map)
                    .toList();
            taskDao.updateTasksOrder(taskDbos);
        }, executorService);
    }

    @Override
    public CompletionStage<List<Task>> findAllTasksInOrderBounds(long checklistId, long taskOrderNumber, Long newOrderNumber) {
        final long upperBound = Math.max(newOrderNumber, taskOrderNumber);
        final long lowerBound = Math.min(newOrderNumber, taskOrderNumber);
        return CompletableFuture.supplyAsync(() ->
                taskDao.findTasksInOrderBounds(checklistId, lowerBound, upperBound)
                        .stream()
                        .map(taskMapper::map)
                        .toList(), executorService);
    }

    @Override
    public CompletionStage<Collection<Task>> saveAll(List<Task> tasks, Long checklistId) {
        return CompletableFuture.supplyAsync(() -> {
                    final List<TaskDbo> taskDbos = tasks.stream().map(taskMapper::map).toList();
                    return taskDao.saveAll(taskDbos, checklistId)
                            .stream().map(taskMapper::map)
                            .toList();
                }, executorService
        );
    }
}
