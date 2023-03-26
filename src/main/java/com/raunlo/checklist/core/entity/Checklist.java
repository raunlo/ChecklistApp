package com.raunlo.checklist.core.entity;


import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.Collection;

@RecordBuilder
public record Checklist(long id, String name, Collection<ChecklistItem> checklistItems) {

}
