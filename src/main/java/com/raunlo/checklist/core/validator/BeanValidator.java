package com.raunlo.checklist.core.validator;

import com.raunlo.checklist.core.entity.error.Errors;
import io.vavr.control.Either;
import java.util.concurrent.CompletionStage;

public interface BeanValidator {
    <T> Either<Errors, Void> validate(T entity);

}
