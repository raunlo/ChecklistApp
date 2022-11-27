package com.raunlo.checklist.resource.mapper;

import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.resource.dto.TaskPredefinedFilterDto;
import org.mapstruct.Mapper;

@Mapper
public interface TaskFilterMapper {

    TaskPredefinedFilter mapFilter(TaskPredefinedFilterDto dto);
}
