package com.raunlo.checklist.persistence;

import com.raunlo.checklist.persistence.model.TaskDbo;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(TaskDbo.class)
public interface TaskDao {

    @SqlQuery("SELECT task_id, task_name, task_completed, order_number FROM TASK  WHERE checklist_id = :checklistId")
    List<TaskDbo> getAllTasks(@Bind("checklistId") Long checklistId);


    @SqlQuery("SELECT task_id, task_name, task_completed, order_number FROM task WHERE checklist_id = :checklistId AND task_id = :taskId")
    Optional<TaskDbo> findById(@Bind("checklistId") Long checklistId, @Bind("taskId") Long taskId);

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlUpdate("DELETE FROM TASK WHERE checklist_id = :checklistId AND task_id = :taskId")
    void deleteById(@Bind("checklistId") Long checklistId, @Bind("taskId") Long taskId);

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlUpdate("UPDATE TASK SET task_name = :taskName AND task_completed = :taskCompleted WHERE checklist_id = :checklistId AND task_id = :taskId")
    void updateTask(@Bind("checklistId") Long checklistId, @Bind("taskId") Long taskId, @Bind("taskCompleted") Boolean taskCompleted, @Bind("taskName") String taskName);

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlUpdate("""
            INSERT INTO TASK(task_id, task_name, task_completed, checklist_id, order_number)
            VALUES(nextval('checklist_sequence'), :taskName, :taskCompleted, :checklistId, (SELECT COUNT(*) + 1 FROM TASK))
            """)
    @GetGeneratedKeys
    TaskDbo insert(@Bind("taskName") String taskName, @Bind("taskCompleted") boolean taskCompleted, @Bind("checklistId") Long checklistId);

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlUpdate("""
            UPDATE task
               SET order_number = CASE order_number
                             WHEN :oldOrderNumber THEN (SELECT order_number FROM task WHERE order_number = :newOrderNumber)
                             WHEN :newOrderNumber THEN (select order_number FROM task WHERE order_number = :oldOrderNumber)
                          end
            WHERE order_number IN (:newOrderNumber,:oldOrderNumber) AND checklist_id= :checklistId;
            """)
    void changeOrderNumbers(@Bind("checklistId") long checklistId, @Bind("oldOrderNumber") long oldOrderNumber, @Bind("newOrderNumber") long newOrderNumber);
}
