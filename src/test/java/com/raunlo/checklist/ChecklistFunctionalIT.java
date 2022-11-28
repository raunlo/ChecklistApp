package com.raunlo.checklist;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.resource.dto.ErrorDto;
import com.raunlo.checklist.resource.dto.TaskDto;
import com.raunlo.checklist.resource.dto.TaskDtoBuilder;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@HelidonTest
@Configuration(configSources = "application.yaml")
public class ChecklistFunctionalIT extends CommonChecklistOperations {

    @Test
    public void successfullyCreatesTask() {
        final Checklist createdChecklist = createChecklist(new Checklist()
                .withName("creates_task"));

        final TaskDto taskDto = TaskDtoBuilder.builder()
                .name("new task")
                .build();
        final TaskDto createsTask = successfullyCreatesTask(createdChecklist.getId(), taskDto);
        final Response getTask = getTask(createdChecklist.getId(), createsTask.id());
        assertThat(getTask)
                .matches(response -> response.getStatus() == 200);

        assertThat(getTask.readEntity(Task.class))
                .matches((Task existingTask) -> existingTask.isCompleted() == createsTask.completed())
                .matches(existingTask -> existingTask.getName().equals(createsTask.name()))
                .matches(existingTask -> existingTask.getId().equals(createsTask.id()));
    }

    @Test
    public void successfullyDeleteTask() {
        final Checklist createdChecklist = createChecklist(new Checklist().withName("deletes_task"));
        final TaskDto taskDto = TaskDtoBuilder.builder()
                .name("new task")
                .build();
        final TaskDto createdTask = successfullyCreatesTask(createdChecklist.getId(), taskDto);
        deleteTask(createdChecklist.getId(), createdTask.id());
        assertThat(getTask(createdChecklist.getId(), createdTask.id()))
                .matches(response -> response.getStatus() == 404);
    }

    @Test
    public void successfullyFindsAllTasks() {
        final Checklist createdChecklist = createChecklist(new Checklist().withName("finds_all_tasks"));

        final TaskDto taskDto1 = TaskDtoBuilder.builder()
                .name("new task")
                .build();
        final TaskDto taskDto2 = TaskDtoBuilder.builder()
                .name("new task2")
                .build();
        final TaskDto taskDto3 = TaskDtoBuilder.builder()
                .name("new task3")
                .build();

        final TaskDto createdTask1 = successfullyCreatesTask(createdChecklist.getId(), taskDto1);
        final TaskDto createdTask2 = successfullyCreatesTask(createdChecklist.getId(), taskDto2);
        final TaskDto createdTask3 = successfullyCreatesTask(createdChecklist.getId(), taskDto3);

        final Collection<TaskDto> allTasks = getAllTasks(createdChecklist.getId(), null);
        assertThat(allTasks).contains(createdTask3, createdTask1, createdTask2);
    }

    @Test
    public void successfullyReturnsCompletedTaskWithCompletedFilter() {
        final Checklist createdChecklist = createChecklist(new Checklist().withName("finds_all_tasks"));

        final TaskDto taskDto1 = TaskDtoBuilder.builder()
                .name("new task")
                .build();
        final TaskDto taskDto2 = TaskDtoBuilder.builder()
                .name("new task2")
                .build();
        final TaskDto taskDto3 = TaskDtoBuilder.builder()
                .name("new task3")
                .build();

        final TaskDto createdTask1 = successfullyCreatesTask(createdChecklist.getId(), taskDto1);
        final TaskDto createdTask2 = successfullyCreatesTask(createdChecklist.getId(), taskDto2);
        final TaskDto createdTask3 = successfullyCreatesTask(createdChecklist.getId(), taskDto3);

        Collection<TaskDto> completedTasks = getAllTasks(createdChecklist.getId(),
                TaskPredefinedFilter.COMPLETED);
        assertThat(completedTasks).isEmpty();

        final TaskDto updatedTask = TaskDtoBuilder.builder(createdTask1).completed(Boolean.TRUE).build();
        updateTask(createdChecklist.getId(), updatedTask);

        completedTasks = getAllTasks(createdChecklist.getId(), TaskPredefinedFilter.COMPLETED);
        assertThat(completedTasks).hasSize(1)
                .contains(updatedTask);
    }

    @Test
    public void successfullyReturnsTODOTaskWithCompletedFilter() {
        final Checklist createdChecklist = createChecklist(new Checklist().withName("finds_all_tasks"));

        final TaskDto taskDto1 = TaskDtoBuilder.builder()
                .name("new task")
                .build();
        final TaskDto taskDto2 = TaskDtoBuilder.builder()
                .name("new task2")
                .build();
        final TaskDto taskDto3 = TaskDtoBuilder.builder()
                .name("new task3")
                .build();

        final TaskDto createdTask1 = successfullyCreatesTask(createdChecklist.getId(), taskDto1);
        final TaskDto createdTask2 = successfullyCreatesTask(createdChecklist.getId(), taskDto2);
        final TaskDto createdTask3 = successfullyCreatesTask(createdChecklist.getId(), taskDto3);

        Collection<TaskDto> completedTasks = getAllTasks(createdChecklist.getId(),
                TaskPredefinedFilter.TODO);
        assertThat(completedTasks).hasSize(3);

        final TaskDto updatedTask = TaskDtoBuilder.builder(createdTask1).completed(Boolean.TRUE).build();
        updateTask(createdChecklist.getId(), updatedTask);

        completedTasks = getAllTasks(createdChecklist.getId(), TaskPredefinedFilter.TODO);
        assertThat(completedTasks).hasSize(2)
                .contains(createdTask3, createdTask2);
    }

    @Test
    public void successfullyUpdatesTask() {
        final Checklist createdChecklist = createChecklist(new Checklist().withName("finds_all_tasks"));
        final TaskDto taskDto = TaskDtoBuilder.builder().name("new task").build();
        final TaskDto createdTask = successfullyCreatesTask(createdChecklist.getId(), taskDto);

        final TaskDto updatedTask = TaskDtoBuilder.builder(createdTask).completed(Boolean.TRUE).build();
        updateTask(createdChecklist.getId(), updatedTask);


        final Response getTask = getTask(createdChecklist.getId(), createdTask.id());
        assertThat(getTask.getStatus()).isEqualTo(200);
        assertThat(getTask.readEntity(Task.class))
                .matches(Task::isCompleted);

    }

    @Test
    public void tooLongTaskNameReturns400() {
        final Checklist createdChecklist = createChecklist(new Checklist().withName("finds_all_tasks"));

        final TaskDto taskDto = TaskDtoBuilder.builder()
                .name("n".repeat(902))
                .build();

        try (final Response taskCreationResponse = createTask(createdChecklist.getId(), taskDto)) {
            assertThat(taskCreationResponse.getStatus())
                    .isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
            final ErrorDto errorDto = taskCreationResponse.readEntity(ErrorDto.class);
            assertThat(errorDto.errorCode()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
            assertThat(errorDto.reason()).isEqualTo("Given task name is too long");
        }
    }
}
