package com.raunlo.checklist.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@With
@AllArgsConstructor
@NotNull
public class Checklist {

    private Collection<Task> tasks;
    private Long id;
    private String description;
    private String name;

    public Collection<Task> getTasks() {
        return tasks;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
