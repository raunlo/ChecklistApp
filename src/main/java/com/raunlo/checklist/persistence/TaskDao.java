package com.raunlo.checklist.persistence;

import com.raunlo.checklist.persistence.model.TaskDbo;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(TaskDbo.class)
public interface TaskDao {

    @SqlQuery("SELECT task_id, task_name, task_completed, order_number FROM TASK  WHERE checklist_id = :checklistId ORDER BY order_number")
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
    @SqlBatch("UPDATE task SET order_number = :task.order where task_id = :task.id")
    void updateTasksOrder(@BindMethods("task") List<TaskDbo> taskDbos);

    @SqlQuery("""
        SELECT task_id, task_name, task_completed, order_number FROM task
            where order_number >= :lowerBound AND order_number <= :upperBound
            ORDER BY order_number
            """)
    List<TaskDbo> findTasksInOrderBounds(@Bind("lowerBound") long lowerBound, @Bind("upperBound") long upperBound);
}
