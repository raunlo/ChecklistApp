package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.repository.TaskRepository;
import com.raunlo.checklist.core.repository.annotation.ChecklistDB;
import com.raunlo.checklist.persistence.mapper.TaskMapper;
import com.raunlo.checklist.persistence.model.TaskDbo;
import com.raunlo.checklist.persistence.util.EntityManagerWrapper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RequestScoped
class TaskRepositoryImpl implements TaskRepository {

    private final EntityManager entityManager;
    private final TaskMapper taskMapper;

    @Inject()
    TaskRepositoryImpl(@ChecklistDB EntityManagerWrapper entityManagerWrapper, TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
        this.entityManager = entityManagerWrapper.getEntityManager();
    }

    @Override
    @Transactional
    public Task save(final Long checklistId, final Task entity) {
        final TaskDbo taskDbo = taskMapper.map(checklistId, entity);
        entityManager.persist(taskDbo);
        return entity.withId(taskDbo.getId());
    }

    @Override
    @Transactional
    public Task update(final Long checklistId, final Task entity) {
        final TaskDbo taskDbo = taskMapper.map(checklistId, entity);
        return taskMapper.map(entityManager.merge(taskDbo));
    }

    @Override
    @Transactional
    public void delete(final Long checklistId, final long id) {
        entityManager.createQuery("DELETE FROM TaskDbo WHERE id = :id AND checklistDbo.id = :checklistId")
                .setParameter("id", id)
                .setParameter("checklistId", checklistId)
                .executeUpdate();
    }

    @Override
    public Optional<Task> findById(final Long checklistId, final long id) {
        return entityManager.createQuery("SELECT t FROM TaskDbo t WHERE t.checklistDbo.id = :checklistId AND t.id = :id", TaskDbo.class)
                .setParameter("checklistId", checklistId)
                .setParameter("id", id)
                .getResultStream().findFirst()
                .map(taskMapper::map);
    }

    @Override
    public Collection<Task> getAll(final Long checklistId) {
        return entityManager.createQuery("SELECT t FROM TaskDbo t WHERE t.checklistDbo.id = :checklistId", TaskDbo.class)
                .setParameter("checklistId", checklistId)
                .getResultList()
                .stream()
                .map(taskMapper::map)
                .collect(toList());
    }
}
