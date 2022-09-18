package com.raunlo.checklist.persistence.mapper;

import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.persistence.model.TaskDbo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ChecklistMapper.class)
public interface TaskMapper {
    @Mapping(source = "taskName", target = "name")
    @Mapping(source = "taskCompleted", target = "completed")
    Task map(TaskDbo taskDbo);

    @Mapping(source = "task.name", target = "taskName")
    @Mapping(source = "task.completed", target = "taskCompleted")

    //@Mapping(source = "checklistId", target = "checklistDbo.id")
    TaskDbo map(Task task);
}
