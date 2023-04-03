package com.raunlo.checklist.persistence.model;

import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

public record ChecklistItemsDbo(@ColumnName("task_id") Long id, @ColumnName("task_name") String taskName,
                                @ColumnName("task_completed") Boolean taskCompleted, @ColumnName("next_task") long next_task_id){
}
