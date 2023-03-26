package com.raunlo.checklist.resource.v1;

import com.google.common.base.Enums;
import com.raunlo.checklist.core.entity.ChangeOrderRequest;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Path("api/v1/checklist/{checklist_id}/task")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
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
        checklistItemService.getAll(checklistId, checklistItemFilterMapper.mapFilter(filterDto));
    return this.mapResponse(allItemsResponse,
        entity -> this.ok(checklistItemDtoMapper.map(entity)));
  }

  @GET()
  @Path("/{task_id}")
  public CompletionStage<Response> findTaskById(@PathParam("task_id") long taskId,
      @PathParam("checklist_id") Long checklistId) {
    final var findByIdResponse = checklistItemService.findById(checklistId, taskId);
    return this.mapResponse(findByIdResponse,
        entity -> this.ok(entity.map(checklistItemDtoMapper::map)));
  }

  @POST()
  public CompletionStage<Response> saveTask(@NotNull @Valid ChecklistItemDto checklistItemDto,
      @PathParam("checklist_id") Long checklistId, @Context UriInfo uriInfo) {
    final ChecklistItem checklistItem = checklistItemDtoMapper.map(checklistItemDto, checklistId);
    final var savedItemResponse = checklistItemService.save(checklistId, checklistItem);

    return this.mapResponse(savedItemResponse,
        savedItem -> created(uriInfo, checklistItemDtoMapper.map(savedItem)));
  }

  @PUT
  @Path("/{id}")
  public CompletionStage<Response> updateTask(@NotNull @Valid ChecklistItemDto checklistItemDto,
      @PathParam("id") Long taskId, @PathParam("checklist_id") Long checklistId) {

    final ChecklistItem checklistItem = checklistItemDtoMapper.map(checklistItemDto, taskId);

    final var itemUpdateResponse = checklistItemService.update(checklistId, checklistItem);

    return this.mapResponse(itemUpdateResponse,
        updatedTask -> this.ok(checklistItemDtoMapper.map(updatedTask)));
  }

  @DELETE
  @Path("/{id}")
  public CompletionStage<Response> deleteTask(@PathParam("id") Long id,
      @PathParam("checklist_id") Long checklistId) {
    final var deleteResponse = checklistItemService.delete(checklistId, id);

    return this.mapResponse(deleteResponse, __ -> Response.noContent().build());
  }

  @PATCH
  @Path("/change-order")
  public CompletionStage<Response> changeTaskOrder(ChangeOrderRequest changeOrderRequest,
      @PathParam("checklist_id") Long checklistId) {
    changeOrderRequest.setChecklistId(checklistId);

    final var response = checklistItemService.changeOrder(changeOrderRequest);
    return mapResponse(response, __ -> this.ok((Object) null));
  }

  @POST
  @Path("/save-multiple")
  public CompletionStage<Response> saveMultiple(
      @NotNull @Valid List<ChecklistItemDto> checklistItemDtos,
      @PathParam("checklist_id") Long checklistId) {

    final List<ChecklistItem> checklistItems = checklistItemDtos.stream()
        .map(checklistItemDto -> checklistItemDtoMapper.map(checklistItemDto, checklistId))
        .toList();

    final var savedItemsResponse = checklistItemService.saveAll(checklistItems, checklistId);

    return this.mapResponse(savedItemsResponse,
        savedItems -> this.ok(checklistItemDtoMapper.map(savedItems)));
  }
}
