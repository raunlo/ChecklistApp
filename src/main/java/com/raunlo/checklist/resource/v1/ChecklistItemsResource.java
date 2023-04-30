package com.raunlo.checklist.resource.v1;

import com.google.common.base.Enums;
import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.ChangeOrderRequestBuilder;
import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.core.service.ChecklistItemService;
import com.raunlo.checklist.resource.BaseResource;
import com.raunlo.checklist.resource.dto.ChecklistItemDto;
import com.raunlo.checklist.resource.dto.TaskPredefinedFilterDto;
import com.raunlo.checklist.resource.dto.error.ClientErrorDtoBuilder;
import com.raunlo.checklist.resource.dto.error.ClientErrorsDto;
import com.raunlo.checklist.resource.dto.error.ClientErrorsDtoBuilder;
import com.raunlo.checklist.resource.mapper.ChecklistItemDtoMapper;
import com.raunlo.checklist.resource.mapper.ChecklistItemFilterMapper;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("api/v1/checklist/{checklist_id}/task")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChecklistItemsResource extends BaseResource {

  private final ChecklistItemService checklistItemService;
  private final ChecklistItemFilterMapper checklistItemFilterMapper;
  private final ChecklistItemDtoMapper checklistItemDtoMapper;

  @Inject()
  public ChecklistItemsResource(final ChecklistItemService checklistItemService,
      ChecklistItemFilterMapper checklistItemFilterMapper,
      ChecklistItemDtoMapper checklistItemDtoMapper) {
    this.checklistItemService = checklistItemService;
    this.checklistItemFilterMapper = checklistItemFilterMapper;
    this.checklistItemDtoMapper = checklistItemDtoMapper;
  }

  @GET()
  public CompletionStage<Response> getTasks(@PathParam("checklist_id") long checklistId,
      @QueryParam("filterType") Optional<String> filterType) {
    final TaskPredefinedFilterDto filterDto = filterType
        .flatMap((String enumValue) ->
            Enums.getIfPresent(TaskPredefinedFilterDto.class, enumValue.toUpperCase()).toJavaUtil())
        .orElse(null);

    if (filterType.isPresent() && filterDto == null) {
      final ClientErrorsDto clientErrorDto = ClientErrorsDtoBuilder.builder()
          .errors(Set.of(
              ClientErrorDtoBuilder.builder()
                  .reason("Invalid filter type")
                  .fieldName("filterType")
                  .build())
          )
          .build();
      return CompletableFuture.completedFuture(
          Response.status(400).entity(clientErrorDto).build());
    }

    final var allItemsResponse =
        CompletableFuture.completedFuture(checklistItemService.getAll(checklistId,
            checklistItemFilterMapper.mapFilter(filterDto)));
    return this.mapResponse(allItemsResponse,
        entity -> this.ok(checklistItemDtoMapper.map(entity)));
  }

  @GET()
  @Path("/{task_id}")
  public CompletionStage<Response> findTaskById(@PathParam("task_id") long taskId,
      @PathParam("checklist_id") Long checklistId) {
    final var findByIdResponse = CompletableFuture.completedFuture(
        checklistItemService.findById(checklistId, taskId));
    return this.mapResponse(findByIdResponse,
        entity -> this.ok(entity.map(checklistItemDtoMapper::map)));
  }

  @POST()
  public CompletionStage<Response> saveTask(@NotNull ChecklistItemDto checklistItemDto,
      @PathParam("checklist_id") Long checklistId, @Context UriInfo uriInfo) {
    final ChecklistItem checklistItem = checklistItemDtoMapper.map(checklistItemDto);
    final var savedItemResponse = CompletableFuture.completedFuture(
        checklistItemService.save(checklistId, checklistItem));

    return this.mapResponse(savedItemResponse,
        savedItem -> created(uriInfo, checklistItemDtoMapper.map(savedItem)));
  }

  @PUT
  @Path("/{id}")
  public CompletionStage<Response> updateTask(@NotNull @Valid ChecklistItemDto checklistItemDto,
      @PathParam("id") Long taskId, @PathParam("checklist_id") Long checklistId) {

    final ChecklistItem checklistItem = checklistItemDtoMapper.map(checklistItemDto, taskId);

    final var itemUpdateResponse = CompletableFuture.completedFuture(
        checklistItemService.update(checklistId, checklistItem));

    return this.mapResponse(itemUpdateResponse,
        updatedTask -> this.ok(checklistItemDtoMapper.map(updatedTask)));
  }

  @DELETE
  @Path("/{id}")
  public CompletionStage<Response> deleteTask(@PathParam("id") Long id,
      @PathParam("checklist_id") Long checklistId) {
    final var deleteResponse = CompletableFuture.completedFuture(
        checklistItemService.delete(checklistId, id));

    return this.mapResponse(deleteResponse, __ -> Response.noContent().build());
  }

  @PATCH
  @Path("/change-order")
  public CompletionStage<Response> changeTaskOrder(@NotNull ChangeOrderRequest changeOrderRequest,
      @PathParam("checklist_id") Long checklistId) {

    final var response = CompletableFuture.completedFuture(checklistItemService.changeOrder(
        ChangeOrderRequestBuilder.builder(changeOrderRequest)
            .checklistId(checklistId)
            .build()));
    return mapResponse(response, __ -> this.noContent());
  }

  @POST
  @Path("/save-multiple")
  public CompletionStage<Response> saveMultiple(
      @NotNull @Valid List<ChecklistItemDto> checklistItemDtos,
      @PathParam("checklist_id") Long checklistId) {

    final List<ChecklistItem> checklistItems = checklistItemDtos.stream()
        .map(checklistItemDto -> checklistItemDtoMapper.map(checklistItemDto, checklistId))
        .toList();

    final var savedItemsResponse = CompletableFuture.completedFuture(
        checklistItemService.saveAll(checklistItems, checklistId));

    return this.mapResponse(savedItemsResponse,
        savedItems -> Response.status(201).entity(checklistItemDtoMapper.map(savedItems)).build());
  }
}
