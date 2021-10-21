package com.raunlo.checklist.core.service;

import java.util.Collection;
import java.util.Optional;

public interface CrudService<T> {

    T save(final T entity);

    T update(final T entity);

    void delete(final int id);

    Optional<T> findById(final int id);

    Collection<T> getAll();
}
