package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import com.raunlo.checklist.persistence.mapper.ChecklistMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collection;
import java.util.Optional;

@ApplicationScoped
class ChecklistRepositoryImpl implements ChecklistRepository {
    private final ChecklistMapper checklistMapper;

    @Inject()
    ChecklistRepositoryImpl(ChecklistMapper checklistMapper) {
        this.checklistMapper = checklistMapper;
    }

    @Override
    public Checklist save(Checklist entity) {
        return null;
    }

    @Override
    public Checklist update(Checklist entity) {
        return null;
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public Optional<Checklist> findById(long id) {
        return Optional.empty();
    }

    @Override
    public Collection<Checklist> getAll() {
        return null;
    }
}
