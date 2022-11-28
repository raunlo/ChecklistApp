package com.raunlo.checklist.core.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Errors {
    public static final String TASK_NAME_IS_TOO_LONG = "Given task name is too long";
}
