package com.raunlo.checklist.core.entity;

import io.soabase.recordbuilder.core.RecordBuilder;


@RecordBuilder
public record ChangeOrderRequest(long checklistItemId, long newOrderNumber, long checklistId) {

}
