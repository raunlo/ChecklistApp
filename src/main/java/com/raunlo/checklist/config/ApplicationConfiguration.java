package com.raunlo.checklist.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.Dependent;
import javax.inject.Singleton;
import javax.ws.rs.Produces;

@Dependent
public class ApplicationConfiguration {

    @Singleton
    @Produces
    public MongoDatabase mongoDatabase(@ConfigProperty(name = "mongo-db-url") String mongoDbUrl, @ConfigProperty(name = "database-name") String databaseName) {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoDbUrl));
        return mongoClient.getDatabase(databaseName);
    }

}
