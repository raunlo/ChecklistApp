package com.raunlo.checklist.core;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import com.raunlo.checklist.core.service.ChecklistService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
        return checklistRepository.saveAsync(entity);
    }

    @Override
    public CompletionStage<Checklist> update(Checklist entity) {
        return checklistRepository.updateAsync(entity);
    }

    @Override
    public CompletionStage<Void> delete(long id) {
        return checklistRepository.deleteAsync(id);
    }

    @Override
    public CompletionStage<Optional<Checklist>> findById(long id) {
        return checklistRepository.findByIdAsync(id);
    }

    @Override
    public CompletionStage<Collection<Checklist>> getAll() {
        return checklistRepository.getAllAsync();
    }
}
