package com.raunlo.checklist;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.entity.Task;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Java6Assertions.assertThat;

@HelidonTest
@Configuration(configSources = "application.yaml")
public class ChecklistFunctionalIT extends CommonChecklistOperations {

    @Test
    public void successfullyCreatesTask() {
        final Checklist createdChecklist = createChecklist(new Checklist()
                .withName("creates_task"));
        final Task createsTask = successfullyCreatesTask(createdChecklist.getId(), new Task()
                .withName("new task"));
        final Response getTask = getTask(createdChecklist.getId(), createsTask.getId());
        assertThat(getTask)
                .matches(response -> response.getStatus() == 200);

        assertThat(getTask.readEntity(Task.class))
                .matches((Task existingTask) -> existingTask.isCompleted() != createsTask.isCompleted())
                .matches(existingTask -> existingTask.getName().equals(createsTask.getName()))
                .matches(existingTask -> existingTask.getId().equals(createsTask.getId()));
    }

    @Test
    public void successfullyDeleteTask() {
        final Checklist createdChecklist = createChecklist(new Checklist().withName("deletes_task"));
        final Task createdTask = successfullyCreatesTask(createdChecklist.getId(), new Task().withName("new task"));
        deleteTask(createdChecklist.getId(), createdTask.getId());
        assertThat(getTask(createdChecklist.getId(), createdTask.getId()))
                .matches(response -> response.getStatus() == 404);
    }

    @Test
    public void successfullyFindsAllTasks() {
        final Checklist createdChecklist = createChecklist(new Checklist().withName("finds_all_tasks"));
        final Task createdTask1 = successfullyCreatesTask(createdChecklist.getId(), new Task().withName("new task"));
        final Task createdTask2 = successfullyCreatesTask(createdChecklist.getId(), new Task().withName("new task2"));
        final Task createdTask3 = successfullyCreatesTask(createdChecklist.getId(), new Task().withName("new task3"));

        final Collection<Task> allTasks = getAllTasks(createdChecklist.getId());
        assertThat(allTasks).contains(createdTask3, createdTask1, createdTask2);
    }

    @Test
    public void successfullyUpdatesTask() {
        final Checklist createdChecklist = createChecklist(new Checklist().withName("finds_all_tasks"));
        final Task createdTask = successfullyCreatesTask(createdChecklist.getId(), new Task().withName("new task"));

        updateTask(createdChecklist.getId(), createdTask.withCompleted(true));
        final Response getTask = getTask(createdChecklist.getId(), createdTask.getId());
        assertThat(getTask.getStatus()).isEqualTo(200);
        assertThat(getTask.readEntity(Task.class))
                .matches(Task::isCompleted);

    }
}
