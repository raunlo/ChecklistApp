package com.raunlo.checklist.core.validator;

import com.raunlo.checklist.core.entity.Error;
import io.vavr.control.Either;

import java.util.concurrent.CompletionStage;

public interface BeanValidator {
    <T> Either<CompletionStage<Error>, T> validate(T entity);
}
