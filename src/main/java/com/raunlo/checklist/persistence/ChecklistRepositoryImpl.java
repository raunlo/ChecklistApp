package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import com.raunlo.checklist.persistence.model.ChecklistDbo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
@NoArgsConstructor
class ChecklistRepositoryImpl implements ChecklistRepository {

    @PersistenceContext(unitName = "checklist_db")
    private EntityManager entityManager;
    private ChecklistMapper checklistMapper;

    @Override
    public Checklist save(Checklist entity) {
        final ChecklistDbo checklistDbo = checklistMapper.map(entity);
        entityManager.persist(checklistDbo);
        return entity.withId(checklistDbo.getId());
    }

    @Override
    public Checklist update(Checklist entity) {
        final ChecklistDbo checklistDbo = checklistMapper.map(entity);
        entityManager.merge(entity);
        return entity;
    }

    @Override
    public void delete(int id) {
        entityManager.createQuery("DELETE FROM ChecklistDbo where id = :id")
                .setParameter("id", id);
    }

    @Override
    public Optional<Checklist> findById(int id) {
        return Optional.ofNullable(entityManager.find(ChecklistDbo.class, id))
                .map(checklistMapper::map);
    }

    @Override
    public Collection<Checklist> getAll() {
        return entityManager.createQuery("SELECT checklist FROM ChecklistDbo checklist", ChecklistDbo.class)
                .getResultList().stream()
                .map(checklistMapper::map)
                .collect(toList());
    }
}
