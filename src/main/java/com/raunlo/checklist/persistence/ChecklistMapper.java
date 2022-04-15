package com.raunlo.checklist.persistence;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.persistence.model.ChecklistDbo;

public interface ChecklistMapper {

    ChecklistDbo map(Checklist checklist);

    Checklist map(ChecklistDbo checklistDbo);
}
