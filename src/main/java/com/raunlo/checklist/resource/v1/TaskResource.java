package com.raunlo.checklist.resource.v1;

import com.google.common.base.Enums;
import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.Task;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.core.service.TaskService;
import com.raunlo.checklist.resource.BaseResource;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.raunlo.checklist.resource.dto.Error;
import com.raunlo.checklist.resource.dto.ErrorBuilder;
import com.raunlo.checklist.resource.dto.TaskPredefinedFilterDto;
import com.raunlo.checklist.resource.mapper.TaskFilterMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("api/v1/checklist/{checklist_id}/task")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TaskResource implements BaseResource {

    private final TaskService taskService;
    private final TaskFilterMapper taskFilterMapper;

    @Inject()
    public TaskResource(final TaskService taskService, TaskFilterMapper taskFilterMapper) {
        this.taskService = taskService;
        this.taskFilterMapper = taskFilterMapper;
    }

    @GET()
    public CompletionStage<Response> getTasks(@PathParam("checklist_id") Long checklistId,
                                              @QueryParam("filterType") Optional<String> filterType) {

        final TaskPredefinedFilterDto filterDto = filterType
                .flatMap((String enumValue) ->
                        Enums.getIfPresent(TaskPredefinedFilterDto.class, enumValue.toUpperCase()).toJavaUtil())
                .orElse(null);

        if (filterType.isPresent() && filterDto == null) {
            final Error error = ErrorBuilder.builder()
                    .errorCode(400)
                    .reason("Invalid filter type")
                    .build();
            return CompletableFuture.completedFuture(
                    Response.status(400).entity(error).build());
        }

        return taskService.getAll(checklistId, taskFilterMapper.mapFilter(filterDto))
                .thenApply(tasks -> Response.status(200).entity(tasks).build());
    }

    @GET()
    @Path("/{task_id}")
    public CompletionStage<Response> findTaskById(@PathParam("task_id") int taskId,
                                                  @PathParam("checklist_id") long checklistId) {
        return taskService.findById(checklistId, taskId)
                .thenApply(this::createResponse);
    }

    @POST()
    public CompletionStage<Response> saveTask(@NotNull @Valid Task task, @PathParam("checklist_id") Long checklistId,
                                              @Context UriInfo uriInfo) {
        return taskService.save(checklistId, task)
                .thenApply(savedTask -> {
                    final URI getResourceURI = uriInfo.getAbsolutePathBuilder().path(String.valueOf(savedTask.getId())).build();
                    return Response.created(getResourceURI).entity(savedTask)
                            .build();
                });
    }

    @PUT
    @Path("/{id}")
    public CompletionStage<Response> updateTask(@NotNull @Valid Task task, @PathParam("id") long taskId,
                                                @PathParam("checklist_id") Long checklistId) {
        task.setId(taskId);
        return taskService.update(checklistId, task)
                .thenApply(updatedTask -> Response.ok().entity(updatedTask).build());
    }

    @DELETE
    @Path("/{id}")
    public CompletionStage<Response> deleteTask(@PathParam("id") int id, @PathParam("checklist_id") Long checklistId) {
        return taskService.delete(checklistId, id)
                .thenApply((__) -> Response.noContent().build());
    }

    @PATCH
    @Path("/change-order")
    public CompletionStage<Response> changeTaskOrder(ChangeOrderRequest changeOrderRequest,
                                                     @PathParam("checklist_id") Long checklistId) {
        changeOrderRequest.setChecklistId(checklistId);
        return taskService.changeOrder(changeOrderRequest)
                .thenApply((__) -> Response.status(200).build());
    }

    @POST
    @Path("/save-multiple")
    public CompletionStage<Response> saveMultiple(@NotNull @Valid List<Task> taskList,
                                                  @PathParam("checklist_id") Long checklistId) {
        return taskService.saveAll(taskList, checklistId)
                .thenApply((final Collection<Task> tasks) -> Response.status(200).entity(tasks).build());
    }
}
