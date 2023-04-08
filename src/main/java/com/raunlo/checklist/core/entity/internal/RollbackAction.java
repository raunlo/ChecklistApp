package com.raunlo.checklist.core.entity.internal;

import com.raunlo.checklist.core.entity.error.Errors;
import io.vavr.control.Either;
import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface RollbackAction<T> {

  CompletionStage<Either<Errors, T>> rollback(final Either<Errors, T> input);
}
