package com.raunlo.checklist.persistence.model;

import com.raunlo.checklist.persistence.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

public record ChecklistDbo(@ColumnName("checklist_id") Long id,
                           @ColumnName("checklist_name") String name,
                           @Nullable Collection<ChecklistItemsDbo> checklistItemsDbos) {

  @JdbiConstructor
  public ChecklistDbo(@ColumnName("checklist_id") Long id,
      @ColumnName("checklist_name") String name) {
    this(id, name, new ArrayList<>());
  }

  public ChecklistDbo addTask(ChecklistItemsDbo checklistItemsDbo) {
    checklistItemsDbos.add(checklistItemsDbo);
    return this;
  }
}
