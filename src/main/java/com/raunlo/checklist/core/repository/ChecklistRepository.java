package com.raunlo.checklist.core.repository;

import com.raunlo.checklist.core.entity.Checklist;

import java.util.Collection;
import java.util.Optional;

public interface ChecklistRepository {

    Checklist save(final Checklist entity);

    Collection<Checklist> saveAll(final Collection<Checklist> entities);

    Checklist update(final Checklist entity);

    void delete(final long id);

    Optional<Checklist> findById(final long id);

    Collection<Checklist> getAll();
}
