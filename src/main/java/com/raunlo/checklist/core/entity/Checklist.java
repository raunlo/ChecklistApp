package com.raunlo.checklist.core.entity;

import java.util.Collection;

public class Checklist {

    private Collection<Task> tasks;
    private String id;
    private String description;
    private String name;

    public Collection<Task> getTasks() {
        return tasks;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public ChecklistBuilder builder() {
        return new ChecklistBuilder();
    }

    public static class ChecklistBuilder {
        private final Checklist checklist;

        public ChecklistBuilder() {
            checklist = new Checklist();
        }

        public ChecklistBuilder withId(final String id) {
            checklist.id = id;
            return this;
        }

        public ChecklistBuilder withName(final String name) {
            checklist.name = name;
            return this;
        }

        public ChecklistBuilder withDescription(final String description) {
            checklist.description = description;
            return this;
        }

        public ChecklistBuilder withTasks(final Collection<Task> tasks) {
            checklist.tasks = tasks;
            return this;
        }

        public Checklist build() {
            return checklist;
        }
    }
}
