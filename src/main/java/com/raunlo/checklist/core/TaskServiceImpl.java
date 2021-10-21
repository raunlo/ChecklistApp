package com.raunlo.checklist.core;

import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.repository.TaskRepository;
import com.raunlo.checklist.core.service.TaskService;
import java.util.Collection;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Inject
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task save(Task entity) {
       return taskRepository.save(entity);
    }

    @Override
    public Task update(Task entity) {
        return taskRepository.update(entity);
    }

    @Override
    public void delete(int id) {
        taskRepository.delete(id);
    }

    @Override
    public Optional<Task> findById(int id) {
       return taskRepository.findById(id);
    }

    @Override
    public Collection<Task> getAll() {
        return null;
    }
}
