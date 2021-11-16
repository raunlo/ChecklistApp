package com.raunlo.checklist.core;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.repository.ChecklistRepository;
import com.raunlo.checklist.core.service.ChecklistService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
class ChecklistServiceImpl extends CrudServiceImpl<Checklist> implements ChecklistService {
    @Inject
    ChecklistServiceImpl(ChecklistRepository checklistRepository) {
        super(checklistRepository);
    }
}
