package com.raunlo.checklist.resource.mapper;

import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.resource.dto.TaskDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TaskMapper {

    TaskDto map(Task task);

    @Mapping(target = "order", ignore = true)
    Task map(TaskDto taskDto);
}
