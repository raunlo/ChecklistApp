package com.raunlo.checklist.persistence.dao;

import com.raunlo.checklist.persistence.model.TaskDbo;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;

@RegisterConstructorMapper(TaskDbo.class)
public interface PostgreChecklistItemDao {

//    @SqlQuery("""
//                            SELECT task_id, task_name, task_completed, order_number, CASE
//                            FROM ITEM
//                            inner join (
//                            SELECT COMPLETED, 'CHECKLIST_ITEM' FROM CHECKLIST_ITEM_PROPERTIES
//                            UNION ALL
//                            SELECT 'EXISTING_ITEM'
//                            )
////
////
////
////
////                            WHERE checklist_id = :checklistId AND
////                                (:filterType IS NULL OR
////                                CASE :filterType
////                                    WHEN 'TODO' THEN task_completed = false
////                                    WHEN 'COMPLETED' then task_completed = true
////                                END)
////
////                            ORDER BY order_number
//
//            """)
//    List<TaskDbo> getAllTasks(@Bind("checklistId") Long checklistId, @Bind("filterType") TaskPredefinedFilter filterType);
//
//
//    @SqlQuery("SELECT task_id, task_name, task_completed, order_number FROM baseItem WHERE checklist_id = :checklistId AND task_id = :taskId")
//    Optional<TaskDbo> findById(@Bind("checklistId") Long checklistId, @Bind("taskId") Long taskId);
//
//    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
//    @SqlUpdate("DELETE FROM TASK WHERE checklist_id = :checklistId AND task_id = :taskId")
//    void deleteById(@Bind("checklistId") Long checklistId, @Bind("taskId") Long taskId);
//
//    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
//    @SqlUpdate("UPDATE TASK SET task_name = :taskName, task_completed = :taskCompleted WHERE checklist_id = :checklistId AND task_id = :taskId")
//    void updateTask(@Bind("checklistId") Long checklistId, @Bind("taskId") Long taskId, @Bind("taskCompleted") Boolean taskCompleted, @Bind("taskName") String taskName);
//
//    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
//    @SqlUpdate("""
//            INSERT INTO TASK(task_id, task_name, task_completed, checklist_id, order_number)
//            VALUES(nextval('checklist_sequence'), :taskName, :taskCompleted, :checklistId, (SELECT COUNT(*) + 1 FROM TASK))
//            """)
//    @GetGeneratedKeys
//    TaskDbo insert(@Bind("taskName") String taskName, @Bind("taskCompleted") boolean taskCompleted, @Bind("checklistId") Long checklistId);
//
//    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
//    @SqlBatch("UPDATE baseItem SET order_number = :baseItem.order where task_id = :baseItem.id")
//    void updateTasksOrder(@BindMethods("baseItem") List<TaskDbo> taskDbos);
//
//    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
//    @SqlBatch("""
//            INSERT INTO baseItem(TASK_ID, TASK_NAME, CHECKLIST_ID, task_completed) VALUES (nextval('checklist_sequence'), :baseItem.taskName, :checklistId, :baseItem.taskCompleted)
//            """)
//    @GetGeneratedKeys()
//    List<TaskDbo> saveAll(@BindMethods("baseItem") List<TaskDbo> taskDbos, @Bind("checklistId") Long checklistId);
//
//    @SqlQuery("""
//            SELECT task_id, task_name, task_completed, order_number FROM baseItem
//                where order_number >= :lowerBound AND order_number <= :upperBound AND checklist_id = :checklistId
//                ORDER BY order_number
//                """)
//    List<TaskDbo> findTasksInOrderBounds(@Bind("checklistId") long checklistId, @Bind("lowerBound") long lowerBound, @Bind("upperBound") long upperBound);
//
//    TaskDbo
}
