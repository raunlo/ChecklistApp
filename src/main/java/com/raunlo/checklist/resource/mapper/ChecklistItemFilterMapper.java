package com.raunlo.checklist.resource.mapper;

import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.resource.dto.TaskPredefinedFilterDto;
import org.mapstruct.Mapper;

@Mapper
public interface ChecklistItemFilterMapper {

    TaskPredefinedFilter mapFilter(TaskPredefinedFilterDto dto);
}
