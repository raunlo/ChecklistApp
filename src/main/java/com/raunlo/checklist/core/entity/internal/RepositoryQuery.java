package com.raunlo.checklist.core.entity.internal;

import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

@FunctionalInterface
public interface RepositoryQuery<T> extends Supplier<CompletionStage<T>> {
}
