package com.raunlo.checklist.persistence.mapper;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.persistence.model.ChecklistDbo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = TaskMapper.class)
public interface ChecklistMapper {

    ChecklistDbo map(Checklist checklist);

    Checklist map(ChecklistDbo checklistDbo);
}
