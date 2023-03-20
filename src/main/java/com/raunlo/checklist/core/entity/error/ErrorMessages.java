package com.raunlo.checklist.core.entity.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {
    public static final String ITEM_NAME_IS_TOO_LONG = "Given task name is too long";
    public static final String CHECKLIST_ITEM_IS_MISSING = "Checklist item is missing";
    public static final String EXISTING_ITEM_IS_MISSING = "Existing item is missing";
}
