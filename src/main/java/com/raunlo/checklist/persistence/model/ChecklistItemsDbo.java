package com.raunlo.checklist.persistence.model;

import org.jdbi.v3.core.mapper.reflect.ColumnName;

public record ChecklistItemsDbo(@ColumnName("task_id") Long id, @ColumnName("task_name") String taskName,
                                @ColumnName("task_completed") Boolean taskCompleted, @ColumnName("next_task") Long nextItemId){
}
