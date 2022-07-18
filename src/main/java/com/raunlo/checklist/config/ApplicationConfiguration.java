package com.raunlo.checklist.config;

import com.raunlo.checklist.core.repository.annotation.ChecklistDB;
import com.raunlo.checklist.persistence.util.EntityManagerWrapper;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Dependent
public class ApplicationConfiguration {

    @PersistenceContext
    private EntityManager em;

    @Produces
    @ChecklistDB
    public EntityManagerWrapper createEntityManager() {
        return new EntityManagerWrapper(em);
    }
}
