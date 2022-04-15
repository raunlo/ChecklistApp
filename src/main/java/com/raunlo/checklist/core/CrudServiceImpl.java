package com.raunlo.checklist.core;

import com.raunlo.checklist.core.repository.CrudRepository;
import com.raunlo.checklist.core.service.CrudService;
import java.util.Collection;
import java.util.Optional;

class CrudServiceImpl<T> implements CrudService<T> {

    private final CrudRepository<T> crudRepository;

    public CrudServiceImpl(CrudRepository<T> crudRepository) {
        this.crudRepository = crudRepository;
    }

    @Override
    public T save(T entity) {
        return crudRepository.save(entity);
    }

    @Override
    public T update(T entity) {
        return crudRepository.update(entity);
    }

    @Override
    public void delete(int id) {
        crudRepository.delete(id);
    }

    @Override
    public Optional<T> findById(int id) {
      return crudRepository.findById(id);
    }

    @Override
    public Collection<T> getAll() {
        return crudRepository.getAll();
    }
}
