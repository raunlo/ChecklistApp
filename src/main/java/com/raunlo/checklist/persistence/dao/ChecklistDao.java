package com.raunlo.checklist.persistence.dao;

import com.raunlo.checklist.persistence.model.ChecklistDbo;
import java.util.List;
import java.util.Optional;


public interface ChecklistDao {

  List<ChecklistDbo> getAllChecklistDbos();
  Long save(ChecklistDbo checklistDbo);

  Boolean checklistExists(long checklistId);

  void updateChecklist(ChecklistDbo checklistDbo);

  void deleteChecklist(long id);

  Optional<ChecklistDbo> findById(long id);
}
