package com.raunlo.checklist;

import static org.assertj.core.api.Assertions.assertThat;

import com.raunlo.checklist.core.entity.BaseItem;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.entity.list.ItemList;
import com.raunlo.checklist.resource.dto.TaskDtoBuilder;
import com.raunlo.checklist.resource.dto.error.ClientErrorDto;
import com.raunlo.checklist.resource.dto.error.ClientErrorsDto;
import com.raunlo.checklist.resource.dto.item.BaseItemDto;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.ws.rs.core.Response;
import java.util.Collection;
import java.util.Set;
import org.junit.jupiter.api.Test;

@HelidonTest
@Configuration(configSources = "application.yaml")
public class BaseItemListFunctionalIT extends CommonChecklistOperations {

    @Test
    public void successfullyCreatesTask() {
        final ItemList createdItemList = createChecklist(new ItemList()
                .withName("creates_task"));

        final BaseItemDto baseItemDto = TaskDtoBuilder.builder()
                .name("new task")
                .build();
        final BaseItemDto createsTask = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto);
        final Response getTask = getTask(createdItemList.getId(), createsTask.getId());
        assertThat(getTask)
                .matches(response -> response.getStatus() == 200);

        assertThat(getTask.readEntity(BaseItem.class))
                .matches((BaseItem existingBaseItem) -> existingBaseItem.isCompleted() == createsTask.completed())
                .matches(existingTask -> existingTask.getName().equals(createsTask.name()))
                .matches(existingTask -> existingTask.getId().equals(createsTask.getId()));
    }

    @Test
    public void successfullyDeleteTask() {
        final ItemList createdItemList = createChecklist(new ItemList().withName("deletes_task"));
        final BaseItemDto baseItemDto = TaskDtoBuilder.builder()
                .name("new task")
                .build();
        final BaseItemDto createdTask = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto);
        deleteTask(createdItemList.getId(), createdTask.getId());
        assertThat(getTask(createdItemList.getId(), createdTask.getId()))
                .matches(response -> response.getStatus() == 404);
    }

    @Test
    public void successfullyFindsAllTasks() {
        final ItemList createdItemList = createChecklist(new ItemList().withName("finds_all_tasks"));

        final BaseItemDto baseItemDto1 = TaskDtoBuilder.builder()
                .name("new task")
                .build();
        final BaseItemDto baseItemDto2 = TaskDtoBuilder.builder()
                .name("new task2")
                .build();
        final BaseItemDto baseItemDto3 = TaskDtoBuilder.builder()
                .name("new task3")
                .build();

        final BaseItemDto createdTask1 = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto1);
        final BaseItemDto createdTask2 = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto2);
        final BaseItemDto createdTask3 = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto3);

        final Collection<BaseItemDto> allTasks = getAllTasks(createdItemList.getId(), null);
        assertThat(allTasks).contains(createdTask3, createdTask1, createdTask2);
    }

    @Test
    public void successfullyReturnsCompletedTaskWithCompletedFilter() {
        final ItemList createdItemList = createChecklist(new ItemList().withName("finds_all_tasks"));

        final BaseItemDto baseItemDto1 = TaskDtoBuilder.builder()
                .name("new task")
                .build();
        final BaseItemDto baseItemDto2 = TaskDtoBuilder.builder()
                .name("new task2")
                .build();
        final BaseItemDto baseItemDto3 = TaskDtoBuilder.builder()
                .name("new task3")
                .build();

        final BaseItemDto createdTask1 = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto1);
        final BaseItemDto createdTask2 = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto2);
        final BaseItemDto createdTask3 = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto3);

        Collection<BaseItemDto> completedTasks = getAllTasks(createdItemList.getId(),
                TaskPredefinedFilter.COMPLETED);
        assertThat(completedTasks).isEmpty();

        final BaseItemDto updatedTask = TaskDtoBuilder.builder(createdTask1).completed(Boolean.TRUE).build();
        updateTask(createdItemList.getId(), updatedTask);

        completedTasks = getAllTasks(createdItemList.getId(), TaskPredefinedFilter.COMPLETED);
        assertThat(completedTasks).hasSize(1)
                .contains(updatedTask);
    }

    @Test
    public void successfullyReturnsTODOTaskWithCompletedFilter() {
        final ItemList createdItemList = createChecklist(new ItemList().withName("finds_all_tasks"));

        final BaseItemDto baseItemDto1 = TaskDtoBuilder.builder()
                .name("new task")
                .build();
        final BaseItemDto baseItemDto2 = TaskDtoBuilder.builder()
                .name("new task2")
                .build();
        final BaseItemDto baseItemDto3 = TaskDtoBuilder.builder()
                .name("new task3")
                .build();

        final BaseItemDto createdTask1 = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto1);
        final BaseItemDto createdTask2 = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto2);
        final BaseItemDto createdTask3 = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto3);

        Collection<BaseItemDto> completedTasks = getAllTasks(createdItemList.getId(),
                TaskPredefinedFilter.TODO);
        assertThat(completedTasks).hasSize(3);

        final BaseItemDto updatedTask = TaskDtoBuilder.builder(createdTask1).completed(Boolean.TRUE).build();
        updateTask(createdItemList.getId(), updatedTask);

        completedTasks = getAllTasks(createdItemList.getId(), TaskPredefinedFilter.TODO);
        assertThat(completedTasks).hasSize(2)
                .contains(createdTask3, createdTask2);
    }

    @Test
    public void successfullyUpdatesTask() {
        final ItemList createdItemList = createChecklist(new ItemList().withName("finds_all_tasks"));
        final BaseItemDto baseItemDto = TaskDtoBuilder.builder().name("new task").build();
        final BaseItemDto createdTask = successfullyCreatesTask(createdItemList.getId(),
          baseItemDto);

        final BaseItemDto updatedTask = TaskDtoBuilder.builder(createdTask).completed(Boolean.TRUE).build();
        updateTask(createdItemList.getId(), updatedTask);


        final Response getTask = getTask(createdItemList.getId(), createdTask.getId());
        assertThat(getTask.getStatus()).isEqualTo(200);
        assertThat(getTask.readEntity(BaseItem.class))
                .matches(BaseItem::isCompleted);

    }

    @Test
    public void tooLongTaskNameReturns400() {
        final ItemList createdItemList = createChecklist(new ItemList().withName("finds_all_tasks"));

        final BaseItemDto baseItemDto = TaskDtoBuilder.builder()
                .name("n".repeat(902))
                .build();

        try (final Response taskCreationResponse = createTask(createdItemList.getId(), baseItemDto)) {
            assertThat(taskCreationResponse.getStatus())
                    .isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
            final ClientErrorsDto clientErrorsDto = taskCreationResponse.readEntity(ClientErrorsDto.class);
            assertThat(clientErrorsDto.errors()).isNotEmpty();
            final Set<ClientErrorDto> errors = clientErrorsDto.errors();
            assertThat(clientErrorsDto.errorCode()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
            assertThat(clientErrorsDto.reason()).isEqualTo("Given task name is too long");
        }
    }
}
