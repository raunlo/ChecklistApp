package com.raunlo.checklist.persistence.dao;

import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.persistence.model.ChecklistItemsDbo;
import java.util.List;
import java.util.Optional;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

@RegisterConstructorMapper(ChecklistItemsDbo.class)
public interface PostgresChecklistItemDao {

    @SqlQuery("""
                            SELECT task_id, task_name, task_completed, order_number
                            FROM TASK
                            WHERE checklist_id = :checklistId AND
                                (:filterType IS NULL OR
                                CASE :filterType
                                    WHEN 'TODO' THEN task_completed = false
                                    WHEN 'COMPLETED' then task_completed = true
                                END)
                            ORDER BY order_number

            """)
    List<ChecklistItemsDbo> getAllTasks(@Bind("checklistId") Long checklistId, @Bind("filterType") TaskPredefinedFilter filterType);


    @SqlQuery("SELECT task_id, task_name, task_completed, order_number FROM task WHERE checklist_id = :checklistId AND task_id = :taskId")
    Optional<ChecklistItemsDbo> findById(@Bind("checklistId") Long checklistId, @Bind("taskId") Long taskId);

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlUpdate("DELETE FROM TASK WHERE checklist_id = :checklistId AND task_id = :taskId")
    void deleteById(@Bind("checklistId") Long checklistId, @Bind("taskId") Long taskId);

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlUpdate("UPDATE TASK SET task_name = :taskName, task_completed = :taskCompleted WHERE checklist_id = :checklistId AND task_id = :taskId")
    void updateTask(@Bind("checklistId") Long checklistId, @Bind("taskId") Long taskId, @Bind("taskCompleted") Boolean taskCompleted, @Bind("taskName") String taskName);

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlUpdate("""
            INSERT INTO TASK(task_id, task_name, task_completed, checklist_id, order_number)
            VALUES(nextval('checklist_sequence'), :taskName, :taskCompleted, :checklistId, (SELECT COUNT(*) + 1 FROM TASK))
            """)
    @GetGeneratedKeys
    ChecklistItemsDbo insert(@Bind("taskName") String taskName, @Bind("taskCompleted") boolean taskCompleted, @Bind("checklistId") Long checklistId);

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlBatch("UPDATE task SET order_number = :task.order where task_id = :task.id")
    void updateTasksOrder(@BindMethods("task") List<ChecklistItemsDbo> checklistItemsDbos);

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlBatch("""
            INSERT INTO task(TASK_ID, TASK_NAME, CHECKLIST_ID, task_completed) VALUES (nextval('checklist_sequence'), :task.taskName, :checklistId, :task.taskCompleted)
            """)
    @GetGeneratedKeys()
    List<ChecklistItemsDbo> saveAll(@BindMethods("task") List<ChecklistItemsDbo> checklistItemsDbos, @Bind("checklistId") Long checklistId);

    @SqlQuery("""
            SELECT task_id, task_name, task_completed, order_number FROM task
                where order_number >= :lowerBound AND order_number <= :upperBound AND checklist_id = :checklistId
                ORDER BY order_number
                """)
    List<ChecklistItemsDbo> findTasksInOrderBounds(@Bind("checklistId") long checklistId, @Bind("lowerBound") long lowerBound, @Bind("upperBound") long upperBound);
}