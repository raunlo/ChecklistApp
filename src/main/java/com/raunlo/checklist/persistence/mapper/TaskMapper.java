package com.raunlo.checklist.persistence.mapper;

import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.persistence.model.TaskDbo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", uses = ChecklistMapper.class)
public interface TaskMapper {

    @Mapping(source = "taskName", target = "name")
    @Mapping(source = "taskCompleted", target = "completed")
    @Mapping(source = "taskComments", target = "description")
    Task map(TaskDbo taskDbo);

    @Mapping(source = "task.name", target = "taskName")
    @Mapping(source = "task.completed", target = "taskCompleted")
    @Mapping(source = "task.description", target = "taskComments")
    @Mapping(source = "checklistId", target = "checklistDbo.id")
    TaskDbo map(Long checklistId, Task task);
}
