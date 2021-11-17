package com.raunlo.checklist.persistence;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Optional;

@ApplicationScoped
class ChecklistRepositoryImpl implements ChecklistRepository {

    private final MongoCollection<Document> collection;
    ChecklistRepositoryImpl(MongoDatabase mongoDatabase) {
       collection = mongoDatabase.getCollection(ChecklistRepository.COLLECTION_NAME);
    }

    @Override
    public Checklist save(Checklist entity) {
        return null;
    }

    @Override
    public Checklist update(Checklist entity) {
        return null;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Optional<Checklist> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Collection<Checklist> getAll() {
        return null;
    }
}
