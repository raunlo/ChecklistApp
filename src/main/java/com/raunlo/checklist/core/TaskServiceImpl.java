package com.raunlo.checklist.core;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.repository.TaskRepository;
import com.raunlo.checklist.core.service.TaskService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

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
    public CompletionStage<Collection<Task>> getAll(final Long checklistId, final TaskPredefinedFilter taskPredefinedFilter) {
        return taskRepository.getAll(checklistId, taskPredefinedFilter);
    }

    @Override
    public CompletionStage<Collection<Task>> saveAll(final List<Task> tasks, Long checklistId) {
        return taskRepository.saveAll(tasks, checklistId);
    }

    @Override
    public CompletionStage<Void> changeOrder(final ChangeOrderRequest changeOrderRequest) {
        return taskRepository.findById(changeOrderRequest.getChecklistId(), changeOrderRequest.getTaskId())
                .thenApply((final Optional<Task> task) -> task.map(Task::getOrder)
                        .orElseThrow(RuntimeException::new)
                )

                .thenCompose((final Long taskOrderNumber) -> taskRepository.findAllTasksInOrderBounds(
                                changeOrderRequest.getChecklistId(),
                                taskOrderNumber,
                                changeOrderRequest.getNewOrderNumber()
                        )

                        .thenCompose((final List<Task> tasks) -> {
                            final List<Task> correctTaskOrder = correctTaskOrder(
                                    tasks,
                                    taskOrderNumber,
                                    changeOrderRequest.getNewOrderNumber());
                            return taskRepository.changeOrder(correctTaskOrder);
                        }));
    }

    private List<Task> correctTaskOrder(List<Task> tasks, long oldOrderNumber, long newOrderNumber) {
        final List<Task> result = new ArrayList<>(tasks.size());
        final boolean taskOrderNumberDecreased = oldOrderNumber < newOrderNumber;

        for (int i = 0; i < tasks.size(); i++) {
            final Task task = tasks.get(i);
            if (i == 0 && taskOrderNumberDecreased) {
                result.add(task.withOrder(newOrderNumber));
                continue;
            }

            if (i == tasks.size() - 1 && !taskOrderNumberDecreased) {
                result.add(task.withOrder(newOrderNumber));
                continue;
            }
            final long newElementOrder = taskOrderNumberDecreased ? task.getOrder() - 1 : task.getOrder() + 1;
            result.add(task.withOrder(newElementOrder));
        }

        return result;
    }
}
