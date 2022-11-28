package com.raunlo.checklist.validator;

import com.raunlo.checklist.core.entity.Error;
import com.raunlo.checklist.core.entity.ErrorType;
import com.raunlo.checklist.core.validator.BeanValidator;
import com.raunlo.checklist.core.entity.ErrorBuilder;
import io.vavr.control.Either;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class BeanBeanValidatorImpl implements BeanValidator {

    private final Validator validator;

    @Inject
    public BeanBeanValidatorImpl(jakarta.validation.Validator validatorBean) {
        this.validator = validatorBean;
    }

    @Override
    public <T> Either<CompletionStage<Error>, T> validate(T entity) {
        final Set<ConstraintViolation<T>> validate = validator.validate(entity);
        final Optional<Error> errorOpt = validate.stream()
                .findFirst()
                .map(violation -> ErrorBuilder.builder().errorMessage(violation.getMessage())
                        .errorType(ErrorType.VALIDATION_ERROR)
                        .build());
        return errorOpt.map(error ->
                        Either.<CompletionStage<Error>, T>left(CompletableFuture.completedFuture(error)))
                .orElseGet(() -> Either.right(entity));
    }
}
