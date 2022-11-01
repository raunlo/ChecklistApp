package com.raunlo.checklist.core;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import com.raunlo.checklist.core.service.ChecklistService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
class ChecklistServiceImpl implements ChecklistService {

    private final ChecklistRepository checklistRepository;

    @Inject
    ChecklistServiceImpl(ChecklistRepository checklistRepository) {
        this.checklistRepository = checklistRepository;
    }

    @Override
    public CompletionStage<Checklist> save(Checklist entity) {
        return checklistRepository.save(entity);
    }

    @Override
    public CompletionStage<Checklist> update(Checklist entity) {
        return checklistRepository.update(entity);
    }

    @Override
    public CompletionStage<Void> delete(long id) {
        return checklistRepository.delete(id);
    }

    @Override
    public CompletionStage<Optional<Checklist>> findById(long id) {
        return checklistRepository.findById(id);
    }

    @Override
    public CompletionStage<Collection<Checklist>> getAll() {
        return checklistRepository.getAll();
    }
}
