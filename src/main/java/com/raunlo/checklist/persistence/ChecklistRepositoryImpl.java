package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import com.raunlo.checklist.core.repository.annotation.ChecklistDB;
import com.raunlo.checklist.persistence.mapper.ChecklistMapper;
import com.raunlo.checklist.persistence.model.ChecklistDbo;
import com.raunlo.checklist.persistence.util.EntityManagerWrapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
class ChecklistRepositoryImpl implements ChecklistRepository {

    private final EntityManager entityManager;
    private final ChecklistMapper checklistMapper;

    @Inject()
    ChecklistRepositoryImpl(@ChecklistDB EntityManagerWrapper entityManagerWrapper, ChecklistMapper checklistMapper) {
        this.checklistMapper = checklistMapper;
        this.entityManager = entityManagerWrapper.getEntityManager();
    }

    @Override
    @Transactional
    public Checklist save(Checklist entity) {
        final ChecklistDbo checklistDbo = checklistMapper.map(entity);
        entityManager.persist(checklistDbo);
        return entity.withId(checklistDbo.getId());
    }

    @Override
    @Transactional
    public Collection<Checklist> saveAll(Collection<Checklist> entities) {
        return entities.stream()
                .map((Checklist entity) -> {
                    final ChecklistDbo dbo = checklistMapper.map(entity);
                    entityManager.persist(dbo);
                    return entity.withId(dbo.getId());
                })
                .collect(toList());
    }

    @Override
    @Transactional
    public Checklist update(Checklist entity) {
        final ChecklistDbo checklistDbo = checklistMapper.map(entity);
        return checklistMapper.map(entityManager.merge(checklistDbo));
    }

    @Override
    @Transactional
    public void delete(long id) {
        entityManager.createQuery("DELETE FROM ChecklistDbo where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public Optional<Checklist> findById(long id) {
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
