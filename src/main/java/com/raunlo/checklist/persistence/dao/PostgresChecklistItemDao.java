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
import org.jdbi.v3.sqlobject.transaction.Transactional;

@RegisterConstructorMapper(ChecklistItemsDbo.class)
public interface PostgresChecklistItemDao extends Transactional<PostgresChecklistItemDao> {

  @SqlQuery("""
      WITH RECURSIVE checklist_items as (
          SELECT task_id, TASK_NAME, TASK_COMPLETED, NEXT_TASK,
           (select count(*) from task where checklist_id = :checklistId) as order_number
          FROM TASK
          WHERE CHECKLIST_ID = :checklistId and NEXT_TASK is null
          
          UNION ALL
          
          SELECT t.TASK_ID, t.TASK_NAME, t.TASK_COMPLETED, t.NEXT_TASK, order_number - 1
          from task as t, checklist_items as c
          where checklist_id = :checklistId and t.next_task = c.task_id
      )
      SELECT task_id, TASK_NAME, TASK_COMPLETED, NEXT_TASK FROM checklist_items
      where (:filterType IS NULL OR
                                CASE :filterType
                                    WHEN 'TODO' THEN task_completed = false
                                    WHEN 'COMPLETED' then task_completed = true
                                END)
      ORDER BY order_number asc
      """)
  List<ChecklistItemsDbo> getAllTasks(@Bind("checklistId") Long checklistId,
      @Bind("filterType") TaskPredefinedFilter filterType);


  @SqlQuery("""
      SELECT task_id, task_name, task_completed, next_task
      FROM task
      WHERE checklist_id = :checklistId AND task_id = :checklistItemId
       """)
  Optional<ChecklistItemsDbo> findById(@Bind("checklistId") Long checklistId,
      @Bind("checklistItemId") Long taskId);

  @Transaction(TransactionIsolationLevel.SERIALIZABLE)
  @SqlUpdate("DELETE FROM TASK WHERE task_id = :checklistItemId AND checklist_id = :checklistId")
  void deleteById(@Bind("checklistId") Long checklistId, @Bind("checklistItemId") Long taskId);

  @Transaction(TransactionIsolationLevel.SERIALIZABLE)
  @SqlUpdate("""
      UPDATE TASK SET task_name = :taskName, task_completed = :taskCompleted
       WHERE checklist_id = :checklistId AND task_id = :checklistItemId""")
  void updateTask(@Bind("checklistId") Long checklistId, @Bind("checklistItemId") Long taskId,
      @Bind("taskCompleted") Boolean taskCompleted, @Bind("taskName") String taskName);

  @Transaction(TransactionIsolationLevel.SERIALIZABLE)
  @SqlUpdate("""
      INSERT INTO TASK(task_id, task_name, task_completed, checklist_id, next_task)
      VALUES(nextval('checklist_sequence'), :checklistItem.taskName,
       :checklistItem.taskCompleted, :checklistId, null);
      """)
  @GetGeneratedKeys
  ChecklistItemsDbo insert(@BindMethods("checklistItem") ChecklistItemsDbo checklistItemsDbo,
      @Bind("checklistId") Long checklistId);

  @Transaction(TransactionIsolationLevel.SERIALIZABLE)
  @SqlBatch("""
      INSERT INTO task(TASK_ID, TASK_NAME, CHECKLIST_ID, task_completed)
       VALUES (nextval('checklist_sequence'), :task.taskName, :checklistId, :task.taskCompleted)
      """)
  @GetGeneratedKeys()
  List<ChecklistItemsDbo> saveAll(@BindMethods("task") List<ChecklistItemsDbo> checklistItemsDbos,
      @Bind("checklistId") Long checklistId);


  @SqlUpdate("""
      UPDATE TASK
       SET NEXT_TASK = (SELECT next_task FROM TASK where checklist_id = :checklistId and task_id = :checklistItemId)
       WHERE checklist_id = :checklistId AND next_task = :checklistItemId
      """)
  @Transaction(TransactionIsolationLevel.SERIALIZABLE)
  void removeTaskFromOrderLink(@Bind("checklistId") long checklistId, @Bind("checklistItemId") long taskId);


  @SqlUpdate("""
      UPDATE task SET next_task = :checklistItemId
       WHERE checklist_id = :checklistId AND next_task IS NULL and task_id <> :checklistItemId
          """)
  @Transaction(TransactionIsolationLevel.SERIALIZABLE)
  void addNewlySavedChecklistItemOrderLink(@Bind("checklistId") long checklistId,
      @Bind("checklistItemId") long checklistItemId);


  @SqlQuery("""
      WITH RECURSIVE checklist_items as (
          SELECT task_id, TASK_NAME, TASK_COMPLETED, NEXT_TASK,
           (select count(*) from task where checklist_id = :checklistId) as order_number
          FROM TASK
          WHERE CHECKLIST_ID = :checklistId and NEXT_TASK is null
          
          UNION ALL
          
          SELECT t.TASK_ID, t.TASK_NAME, t.TASK_COMPLETED, t.NEXT_TASK, order_number - 1
          from task as t, checklist_items as c
          where checklist_id = :checklistId and t.next_task = c.task_id
      ), items_ordered as ( SELECT task_id, TASK_NAME, TASK_COMPLETED, NEXT_TASK, order_number FROM checklist_items
          ORDER BY order_number asc)
          
          select CASE 
                  WHEN :orderNumber = 1 then task_id 
                  else next_task end from items_ordered
        where order_number = (
                          CASE
                          when :orderNumber = 1 then 1
                          WHEN (select order_number from items_ordered where task_id = :checklistItemId) > :orderNumber then abs(:orderNumber - 1)
                          else :orderNumber end
          )
      """)
  Optional<Long> findChecklistItemByOrder(@Bind("checklistId") long checklistId,
      @Bind("orderNumber") long orderNumber, @Bind("checklistItemId") long checklistItemId);

  default void updateChecklistItemOrder(long checklistId, long checklistItemId,
      Long newNexChecklistItemId) {
    getHandle().getJdbi().onDemand(PostgresChecklistItemDao.class)
        .useTransaction(TransactionIsolationLevel.SERIALIZABLE, tx -> {
          tx.removeTaskFromOrderLink(checklistId, checklistItemId);
          var handle = tx.getHandle();
          handle.createUpdate(createUpdateChecklistItemPreviousItemOrderLinkQuery(checklistId,
                  checklistItemId, newNexChecklistItemId))
              .execute();
          handle.createUpdate(createUpdateChecklistItemOrderLinkQuery(checklistId, checklistItemId,
                  newNexChecklistItemId))
              .execute();
        });
  }

  private String createUpdateChecklistItemOrderLinkQuery(long checklistId, Long checklistItemId,
      Long newNexChecklistItemId) {
    return "UPDATE TASK SET next_task = " + newNexChecklistItemId
        + "\n WHERE checklist_id = " + checklistId + "and task_id = " + checklistItemId;
  }

  private String createUpdateChecklistItemPreviousItemOrderLinkQuery(long checklistId,
      long checklistItemId,
      Long newNexChecklistItemId) {
    return "UPDATE TASK SET next_task = " + checklistItemId
        + "\n WHERE checklist_id = " + checklistId
        + "and task_id = (select task_id from task where next_task = " + newNexChecklistItemId
        + " and checklist_id = " + checklistId + ")";
  }

}
