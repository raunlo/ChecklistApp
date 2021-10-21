package com.raunlo.checklist.core.repository;

import java.util.Collection;
import java.util.Optional;

public interface CrudRepository<T> {

    T save(final T entity);

    T update(final T entity);

    void delete(final int id);

    Optional<T> findById(final int id);

    Collection<T> getAll();
}
