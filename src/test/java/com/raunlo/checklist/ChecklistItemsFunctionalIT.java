package com.raunlo.checklist;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;

import com.raunlo.checklist.core.entity.ChangeOrderRequestBuilder;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.resource.dto.ChecklistDto;
import com.raunlo.checklist.resource.dto.ChecklistDtoBuilder;
import com.raunlo.checklist.resource.dto.ChecklistItemDto;
import com.raunlo.checklist.resource.dto.ChecklistItemDtoBuilder;
import com.raunlo.checklist.resource.dto.error.ClientErrorsDto;
import com.raunlo.checklist.testcontainers.PostgresSQLTestContainer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
@QuarkusTestResource(PostgresSQLTestContainer.class)
public class ChecklistItemsFunctionalIT extends CommonChecklistOperations {

  @Test
  public void successfullyCreatesTask() {
    final ChecklistDto checklistDto = createChecklist(ChecklistDtoBuilder.builder()
        .name("creates_task").build());

    final ChecklistItemDto checklistItemDto = ChecklistItemDtoBuilder.builder()
        .name("new task")
        .build();
    final ChecklistItemDto createdTask = successfullyCreatesChecklistItem(checklistDto.id(),
        checklistItemDto);
    final Response getTask = getTask(checklistDto.id(), createdTask.id());

    assertThat(getTask.as(ChecklistItemDto.class))
        .matches((ChecklistItemDto existingBaseItem) -> existingBaseItem.completed()
            == createdTask.completed())
        .matches(existingTask -> existingTask.name().equals(createdTask.name()))
        .matches(existingTask -> existingTask.id().equals(createdTask.id()));
  }

  @Test
  public void successfullyDeleteTask() {
    final ChecklistDto checklistDto = createChecklist(
        ChecklistDtoBuilder.builder().name("deletes_task").build());
    final ChecklistItemDto checklistITemDto = ChecklistItemDtoBuilder.builder()
        .name("new task")
        .build();
    final ChecklistItemDto createdTask = successfullyCreatesChecklistItem(checklistDto.id(),
        checklistITemDto);
    deleteTask(checklistDto.id(), createdTask.id());
    assertThat(getTask(checklistDto.id(), createdTask.id()))
        .matches(response -> response.statusCode() == 404);
  }

  @Test
  public void successfullyFindsAllTasks() {
    final ChecklistDto createdItemList = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());

    final ChecklistItemDto checklistItemDto1 = ChecklistItemDtoBuilder.builder()
        .name("new task")
        .build();
    final ChecklistItemDto checklistItemDto2 = ChecklistItemDtoBuilder.builder()
        .name("new task2")
        .build();
    final ChecklistItemDto checklistItemDto3 = ChecklistItemDtoBuilder.builder()
        .name("new task3")
        .build();

    final ChecklistItemDto createdTask1 = successfullyCreatesChecklistItem(createdItemList.id(),
        checklistItemDto1);
    final ChecklistItemDto createdTask2 = successfullyCreatesChecklistItem(createdItemList.id(),
        checklistItemDto2);
    final ChecklistItemDto createdTask3 = successfullyCreatesChecklistItem(createdItemList.id(),
        checklistItemDto3);

    final Collection<ChecklistItemDto> allTasks = getAllChecklistItems(createdItemList.id(), null);
    assertThat(allTasks).containsExactly(createdTask1, createdTask2, createdTask3);
  }

  @Test
  public void successfullyReturnsCompletedTaskWithCompletedFilter() {
    final ChecklistDto createdItemList = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());

    final ChecklistItemDto checklistItemDto1 = ChecklistItemDtoBuilder.builder()
        .name("new task")
        .build();
    final ChecklistItemDto checklistItemDto2 = ChecklistItemDtoBuilder.builder()
        .name("new task2")
        .build();
    final ChecklistItemDto checklistItemDto3 = ChecklistItemDtoBuilder.builder()
        .name("new task3")
        .build();

    final ChecklistItemDto createdTask1 = successfullyCreatesChecklistItem(createdItemList.id(),
        checklistItemDto1);
    final ChecklistItemDto createdTask2 = successfullyCreatesChecklistItem(createdItemList.id(),
        checklistItemDto2);
    final ChecklistItemDto createdTask3 = successfullyCreatesChecklistItem(createdItemList.id(),
        checklistItemDto3);

    Collection<ChecklistItemDto> completedTasks = getAllChecklistItems(createdItemList.id(),
        TaskPredefinedFilter.COMPLETED);
    assertThat(completedTasks).isEmpty();

    final ChecklistItemDto updatedTask = ChecklistItemDtoBuilder.builder(createdTask1)
        .completed(Boolean.TRUE)
        .build();
    updateTask(createdItemList.id(), updatedTask);

    completedTasks = getAllChecklistItems(createdItemList.id(), TaskPredefinedFilter.COMPLETED);
    assertThat(completedTasks).hasSize(1)
        .contains(updatedTask);
  }

  @Test
  public void successfullyReturnsTODOTaskWithCompletedFilter() {
    final ChecklistDto createdItemList = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());

    final ChecklistItemDto checklistItemDto1 = ChecklistItemDtoBuilder.builder()
        .name("new task")
        .build();
    final ChecklistItemDto checklistItemDto2 = ChecklistItemDtoBuilder.builder()
        .name("new task2")
        .build();
    final ChecklistItemDto checklistItemDto3 = ChecklistItemDtoBuilder.builder()
        .name("new task3")
        .build();

    final ChecklistItemDto createdTask1 = successfullyCreatesChecklistItem(createdItemList.id(),
        checklistItemDto1);
    final ChecklistItemDto createdTask2 = successfullyCreatesChecklistItem(createdItemList.id(),
        checklistItemDto2);
    final ChecklistItemDto createdTask3 = successfullyCreatesChecklistItem(createdItemList.id(),
        checklistItemDto3);

    Collection<ChecklistItemDto> completedTasks = getAllChecklistItems(createdItemList.id(),
        TaskPredefinedFilter.TODO);
    assertThat(completedTasks).hasSize(3);

    final ChecklistItemDto updatedTask = ChecklistItemDtoBuilder.builder(createdTask1)
        .completed(Boolean.TRUE)
        .build();
    updateTask(createdItemList.id(), updatedTask);

    completedTasks = getAllChecklistItems(createdItemList.id(), TaskPredefinedFilter.TODO);
    assertThat(completedTasks).hasSize(2)
        .containsExactly(createdTask2, createdTask3);
  }

  @Test
  public void successfullyUpdatesTask() {
    final ChecklistDto createdItemList = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());
    final ChecklistItemDto checklistDto = ChecklistItemDtoBuilder.builder().name("new task")
        .build();
    final ChecklistItemDto createdTask = successfullyCreatesChecklistItem(createdItemList.id(),
        checklistDto);

    final ChecklistItemDto updatedTask = ChecklistItemDtoBuilder.builder(createdTask)
        .completed(Boolean.TRUE)
        .build();
    updateTask(createdItemList.id(), updatedTask);

    final Response getTask = getTask(createdItemList.id(), createdTask.id());
    assertThat(getTask.statusCode()).isEqualTo(200);
    assertThat(getTask.as(ChecklistItemDto.class))
        .matches(ChecklistItemDto::completed);

  }

  @Test
  public void tooLongTaskNameReturns400() {
    final ChecklistDto createdItemList = createChecklist(
        ChecklistDtoBuilder.builder().name("too_long_checklist_name").build());

    final ChecklistItemDto baseItemDto = ChecklistItemDtoBuilder.builder()
        .name("n".repeat(902))
        .build();

    final Response taskCreationResponse = createChecklistItem(createdItemList.id(),
        baseItemDto);
    assertThat(taskCreationResponse.statusCode())
        .isEqualTo(BAD_REQUEST.getStatusCode());
    final ClientErrorsDto clientErrorsDto = taskCreationResponse.as(
        ClientErrorsDto.class);
    assertThat(clientErrorsDto.errors()).isNotEmpty().hasSize(1);
    final var errorDto = new ArrayList<>(clientErrorsDto.errors()).get(0);
    assertThat(errorDto.reason()).isEqualTo("Given task name is too long");

  }

  @Test
  void taskOrderUpdatedSuccessfully_fromUpperListToLowerList() {
    final ChecklistDto checklistDto = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());

    final var checklistItemDto = successfullyCreatesChecklistItem(checklistDto.id(),
        ChecklistItemDtoBuilder.builder()
            .name("First item")
            .build());
    final var checklistItemDto1 = successfullyCreatesChecklistItem(checklistDto.id(),
        ChecklistItemDtoBuilder.builder()
            .name("Second item")
            .build());
    final var checklistItemDto3 = successfullyCreatesChecklistItem(checklistDto.id(),
        ChecklistItemDtoBuilder.builder()
            .name("Third item")
            .build());

    final var updatedOrderChecklist1 = getAllChecklistItems(checklistDto.id(),
        null);

    final var changeOrderRequest = ChangeOrderRequestBuilder.builder()
        .checklistId(checklistDto.id())
        .checklistItemId(checklistItemDto.id())
        .newOrderNumber(3)
        .build();
    updateTaskOrder(changeOrderRequest);

    final var updatedOrderChecklist = getAllChecklistItems(checklistDto.id(),
        null);

    assertThat(updatedOrderChecklist).containsExactly(checklistItemDto1, checklistItemDto3,
        checklistItemDto);
  }

  @Test
  void taskOrderUpdatedSuccessfully_fromFromLowerListToUpperList() {
    final ChecklistDto checklistDto = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());

    final var checklistItemDto = successfullyCreatesChecklistItem(checklistDto.id(),
        ChecklistItemDtoBuilder.builder()
            .name("First item")
            .build());
    final var checklistItemDto1 = successfullyCreatesChecklistItem(checklistDto.id(),
        ChecklistItemDtoBuilder.builder()
            .name("Second item")
            .build());
    final var checklistItemDto3 = successfullyCreatesChecklistItem(checklistDto.id(),
        ChecklistItemDtoBuilder.builder()
            .name("Third item")
            .build());
    final var checklistItemDto4 = successfullyCreatesChecklistItem(checklistDto.id(),
        ChecklistItemDtoBuilder.builder()
            .name("Fourth item")
            .build());

    final var changeOrderRequest = ChangeOrderRequestBuilder.builder()
        .checklistId(checklistDto.id())
        .checklistItemId(checklistItemDto4.id())
        .newOrderNumber(2)
        .build();
    updateTaskOrder(changeOrderRequest);

    final var updatedOrderChecklist = getAllChecklistItems(checklistDto.id(),
        null);

    assertThat(updatedOrderChecklist).containsExactly(checklistItemDto, checklistItemDto4,
        checklistItemDto1,
        checklistItemDto3);
  }

  @Test
  void taskOrderUpdatedSuccessfully_bringItemToFirstItem() {
    final ChecklistDto checklistDto = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());

    final var checklistItemDto = successfullyCreatesChecklistItem(checklistDto.id(),
        ChecklistItemDtoBuilder.builder()
            .name("First item")
            .build());
    final var checklistItemDto1 = successfullyCreatesChecklistItem(checklistDto.id(),
        ChecklistItemDtoBuilder.builder()
            .name("Second item")
            .build());
    final var checklistItemDto3 = successfullyCreatesChecklistItem(checklistDto.id(),
        ChecklistItemDtoBuilder.builder()
            .name("Third item")
            .build());
    final var checklistItemDto4 = successfullyCreatesChecklistItem(checklistDto.id(),
        ChecklistItemDtoBuilder.builder()
            .name("Fourth item")
            .build());

    final var changeOrderRequest = ChangeOrderRequestBuilder.builder()
        .checklistId(checklistDto.id())
        .checklistItemId(checklistItemDto4.id())
        .newOrderNumber(1)
        .build();
    updateTaskOrder(changeOrderRequest);

    final var updatedOrderChecklist = getAllChecklistItems(checklistDto.id(),
        null);

    assertThat(updatedOrderChecklist).containsExactly(checklistItemDto4, checklistItemDto,
        checklistItemDto1,
        checklistItemDto3);
  }


  @Test
  void successfullySavesMultipleTaskAtOnce() {
    final ChecklistDto checklistDto = createChecklist(
        ChecklistDtoBuilder.builder().name("finds_all_tasks").build());

    final var newChecklistItems = List.of(
        ChecklistItemDtoBuilder.builder()
            .name("First item")
            .build(),
        ChecklistItemDtoBuilder.builder()
            .name("Second item")
            .build(),
        ChecklistItemDtoBuilder.builder()
            .name("Third item")
            .build(),
        ChecklistItemDtoBuilder.builder()
            .name("Fourth item")
            .build());


    saveMultipleChecklistItems(checklistDto.id(), newChecklistItems);
    final var updatedOrderChecklist = getAllChecklistItems(checklistDto.id(),
        null);

    assertThat(updatedOrderChecklist).hasSize(4);
  }
}
