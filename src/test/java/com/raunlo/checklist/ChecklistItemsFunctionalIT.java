package com.raunlo.checklist;

import static org.assertj.core.api.Assertions.assertThat;

import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.resource.dto.ChecklistDto;
import com.raunlo.checklist.resource.dto.ChecklistDtoBuilder;
import com.raunlo.checklist.resource.dto.ChecklistItemDto;
import com.raunlo.checklist.resource.dto.ChecklistItemDtoBuilder;
import com.raunlo.checklist.resource.dto.error.ClientErrorsDto;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.Test;

@HelidonTest
@Configuration(configSources = "application.yaml")
public class ChecklistItemsFunctionalIT extends CommonChecklistOperations {

  @Test
  public void successfullyCreatesTask() {
    final ChecklistDto checklistDto = createChecklist(ChecklistDtoBuilder.builder()
        .name("creates_task").build());

    final ChecklistItemDto checklistItemDto = ChecklistItemDtoBuilder.builder()
        .name("new task")
        .build();
    final ChecklistItemDto createsTask = successfullyCreatesTask(checklistDto.id(),
        checklistItemDto);
    final Response getTask = getTask(checklistDto.id(), createsTask.id());
    assertThat(getTask)
        .matches(response -> response.getStatus() == 200);

    assertThat(getTask.readEntity(ChecklistItemDto.class))
        .matches((ChecklistItemDto existingBaseItem) -> existingBaseItem.completed()
            == createsTask.completed())
        .matches(existingTask -> existingTask.name().equals(createsTask.name()))
        .matches(existingTask -> existingTask.id().equals(createsTask.id()));
  }

  @Test
  public void successfullyDeleteTask() {
    final ChecklistDto createdItemList = createChecklist(
        ChecklistDtoBuilder.builder().name("deletes_task").build());
    final ChecklistItemDto baseItemDto = ChecklistItemDtoBuilder.builder()
        .name("new task")
        .build();
    final ChecklistItemDto createdTask = successfullyCreatesTask(createdItemList.id(),
        baseItemDto);
    deleteTask(createdItemList.id(), createdTask.id());
    assertThat(getTask(createdItemList.id(), createdTask.id()))
        .matches(response -> response.getStatus() == 404);
  }

  @Test
  public void successfullyFindsAllTasks() {
    final ChecklistDto createdItemList = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());

    final ChecklistItemDto baseItemDto1 = ChecklistItemDtoBuilder.builder()
        .name("new task")
        .build();
    final ChecklistItemDto baseItemDto2 = ChecklistItemDtoBuilder.builder()
        .name("new task2")
        .build();
    final ChecklistItemDto baseItemDto3 = ChecklistItemDtoBuilder.builder()
        .name("new task3")
        .build();

    final ChecklistItemDto createdTask1 = successfullyCreatesTask(createdItemList.id(),
        baseItemDto1);
    final ChecklistItemDto createdTask2 = successfullyCreatesTask(createdItemList.id(),
        baseItemDto2);
    final ChecklistItemDto createdTask3 = successfullyCreatesTask(createdItemList.id(),
        baseItemDto3);

    final Collection<ChecklistItemDto> allTasks = getAllTasks(createdItemList.id(), null);
    assertThat(allTasks).contains(createdTask3, createdTask1, createdTask2);
  }

  @Test
  public void successfullyReturnsCompletedTaskWithCompletedFilter() {
    final ChecklistDto createdItemList = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());

    final ChecklistItemDto baseItemDto1 = ChecklistItemDtoBuilder.builder()
        .name("new task")
        .build();
    final ChecklistItemDto baseItemDto2 = ChecklistItemDtoBuilder.builder()
        .name("new task2")
        .build();
    final ChecklistItemDto baseItemDto3 = ChecklistItemDtoBuilder.builder()
        .name("new task3")
        .build();

    final ChecklistItemDto createdTask1 = successfullyCreatesTask(createdItemList.id(),
        baseItemDto1);
    final ChecklistItemDto createdTask2 = successfullyCreatesTask(createdItemList.id(),
        baseItemDto2);
    final ChecklistItemDto createdTask3 = successfullyCreatesTask(createdItemList.id(),
        baseItemDto3);

    Collection<ChecklistItemDto> completedTasks = getAllTasks(createdItemList.id(),
        TaskPredefinedFilter.COMPLETED);
    assertThat(completedTasks).isEmpty();

    final ChecklistItemDto updatedTask = ChecklistItemDtoBuilder.builder(createdTask1)
        .completed(Boolean.TRUE)
        .build();
    updateTask(createdItemList.id(), updatedTask);

    completedTasks = getAllTasks(createdItemList.id(), TaskPredefinedFilter.COMPLETED);
    assertThat(completedTasks).hasSize(1)
        .contains(updatedTask);
  }

  @Test
  public void successfullyReturnsTODOTaskWithCompletedFilter() {
    final ChecklistDto createdItemList = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());

    final ChecklistItemDto baseItemDto1 = ChecklistItemDtoBuilder.builder()
        .name("new task")
        .build();
    final ChecklistItemDto baseItemDto2 = ChecklistItemDtoBuilder.builder()
        .name("new task2")
        .build();
    final ChecklistItemDto baseItemDto3 = ChecklistItemDtoBuilder.builder()
        .name("new task3")
        .build();

    final ChecklistItemDto createdTask1 = successfullyCreatesTask(createdItemList.id(),
        baseItemDto1);
    final ChecklistItemDto createdTask2 = successfullyCreatesTask(createdItemList.id(),
        baseItemDto2);
    final ChecklistItemDto createdTask3 = successfullyCreatesTask(createdItemList.id(),
        baseItemDto3);

    Collection<ChecklistItemDto> completedTasks = getAllTasks(createdItemList.id(),
        TaskPredefinedFilter.TODO);
    assertThat(completedTasks).hasSize(3);

    final ChecklistItemDto updatedTask = ChecklistItemDtoBuilder.builder(createdTask1)
        .completed(Boolean.TRUE)
        .build();
    updateTask(createdItemList.id(), updatedTask);

    completedTasks = getAllTasks(createdItemList.id(), TaskPredefinedFilter.TODO);
    assertThat(completedTasks).hasSize(2)
        .contains(createdTask3, createdTask2);
  }

  @Test
  public void successfullyUpdatesTask() {
    final ChecklistDto createdItemList = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());
    final ChecklistItemDto checklistDto = ChecklistItemDtoBuilder.builder().name("new task")
        .build();
    final ChecklistItemDto createdTask = successfullyCreatesTask(createdItemList.id(),
        checklistDto);

    final ChecklistItemDto updatedTask = ChecklistItemDtoBuilder.builder(createdTask)
        .completed(Boolean.TRUE)
        .build();
    updateTask(createdItemList.id(), updatedTask);

    final Response getTask = getTask(createdItemList.id(), createdTask.id());
    assertThat(getTask.getStatus()).isEqualTo(200);
    assertThat(getTask.readEntity(ChecklistItemDto.class))
        .matches(ChecklistItemDto::completed);

  }

  @Test
  public void tooLongTaskNameReturns400() {
    final ChecklistDto createdItemList = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());

    final ChecklistItemDto baseItemDto = ChecklistItemDtoBuilder.builder()
        .name("n".repeat(902))
        .build();

    try (final Response taskCreationResponse = createTask(createdItemList.id(), baseItemDto)) {
      assertThat(taskCreationResponse.getStatus())
          .isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
      final ClientErrorsDto clientErrorsDto = taskCreationResponse.readEntity(
          ClientErrorsDto.class);
      assertThat(clientErrorsDto.errors()).isNotEmpty().hasSize(1);
      final var errorDto = new ArrayList<>(clientErrorsDto.errors()).get(0);
      assertThat(errorDto.reason()).isEqualTo("Given task name is too long");
    }
  }
}
