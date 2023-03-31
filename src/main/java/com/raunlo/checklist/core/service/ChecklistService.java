package com.raunlo.checklist.core.service;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.entity.error.Errors;
import io.vavr.control.Either;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ChecklistService {
    CompletionStage<Either<Errors, Checklist>> save(final Checklist entity);

    CompletionStage<Either<Errors, Checklist>> update(final Checklist entity);

    CompletionStage<Either<Errors, Void>> delete(final Long id);

    CompletionStage<Either<Errors, Optional<Checklist>>> findById(final Long id);

    CompletionStage<Either<Errors, Collection<Checklist>>> getAll();
}
