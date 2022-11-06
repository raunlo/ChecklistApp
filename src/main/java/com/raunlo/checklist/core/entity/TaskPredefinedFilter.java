package com.raunlo.checklist.core.entity;

public enum TaskPredefinedFilter {
    TODO, NONE;

    public boolean getTaskFilter(final Task task) {
        switch (this) {
            case TODO -> {
                return !task.isCompleted();
            }
            case NONE -> {
                return true;
            }
        }
        return true;
    }
}
