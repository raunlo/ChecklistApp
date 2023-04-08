package com.raunlo.checklist.core;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import com.raunlo.checklist.core.service.ChecklistService;
import io.vavr.control.Either;
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
    public CompletionStage<Either<Errors, Checklist>> save(Checklist entity) {
        return checklistRepository.save(entity)
            .thenApply(Either::right);
    }

    @Override
    public CompletionStage<Either<Errors, Checklist>> update(Checklist entity) {
        return checklistRepository.update(entity)
            .thenApply(Either::right);
    }

    @Override
    public CompletionStage<Either<Errors, Void>> delete(Long id) {
        return checklistRepository.delete(id)
            .thenApply(Either::right);
    }

    @Override
    public CompletionStage<Either<Errors, Optional<Checklist>>> findById(Long id) {
        return checklistRepository.findById(id)
            .thenApply(Either::right);
    }

    @Override
    public CompletionStage<Either<Errors, Collection<Checklist>>> getAll() {
        return checklistRepository.getAll()
            .thenApply(Either::right);
    }
}
