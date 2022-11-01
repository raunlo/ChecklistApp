package com.raunlo.checklist.persistence.model;

import com.raunlo.checklist.persistence.annotation.Nullable;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.util.ArrayList;
import java.util.Collection;

public record ChecklistDbo(@ColumnName("checklist_id") Long id, @ColumnName("checklist_name") String name, @Nullable Collection<TaskDbo> taskDbos) {
    @JdbiConstructor
    public ChecklistDbo(@ColumnName("checklist_id") Long id, @ColumnName("checklist_name") String name) {
        this(id, name, new ArrayList<>());
    }
    public ChecklistDbo addTask(TaskDbo taskDbo) {
        taskDbos.add(taskDbo);
        return this;
    }
}
