package com.raunlo.checklist.persistence.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static com.raunlo.checklist.persistence.model.TaskDbo.TABLE_NAME;

@Entity(name = "TaskDbo")
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Table(name = TABLE_NAME)
public class TaskDbo {
    static final String TABLE_NAME = "TASK";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = "checklist_sequence", allocationSize = 1)
    @Column(name = "task_id")
    private Long id;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "task_completed")
    private Boolean taskCompleted;

    @Column(name = "additional_comments")
    private String taskComments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id")
    @ToString.Exclude
    private ChecklistDbo checklistDbo;
}
