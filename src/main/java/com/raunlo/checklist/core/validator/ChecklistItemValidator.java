package com.raunlo.checklist.core.validator;

import com.raunlo.checklist.core.entity.error.Errors;
import io.vavr.control.Either;

public interface ChecklistItemValidator {
    Either<Errors, Void> validateChecklistExistence(Long id);

}
