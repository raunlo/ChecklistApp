package com.raunlo.checklist.resource.v1;

import com.google.common.base.Enums;
import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.ChecklistItem;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.core.service.ChecklistItemService;
import com.raunlo.checklist.resource.BaseResource;
import com.raunlo.checklist.resource.dto.TaskPredefinedFilterDto;
import com.raunlo.checklist.resource.dto.error.ClientErrorDtoBuilder;
import com.raunlo.checklist.resource.dto.error.ClientErrorsDto;
import com.raunlo.checklist.resource.dto.error.ClientErrorsDtoBuilder;
import com.raunlo.checklist.resource.dto.item.BaseItemDto;
import com.raunlo.checklist.resource.dto.item.ChecklistItemDto;
import com.raunlo.checklist.resource.mapper.ChecklistItemFilterMapper;
import com.raunlo.checklist.resource.mapper.ChecklistItemMapper;
import io.vavr.control.Either;
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
import java.util.Collection;
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
  private final ChecklistItemMapper checklistItemMapper;

  @Inject()
  public ChecklistItemsResource(final ChecklistItemService checklistItemService,
    ChecklistItemFilterMapper checklistItemFilterMapper, ChecklistItemMapper checklistItemMapper) {
    this.checklistItemService = checklistItemService;
    this.checklistItemFilterMapper = checklistItemFilterMapper;
    this.checklistItemMapper = checklistItemMapper;
  }

  @GET()
  public CompletionStage<Response> getTasks(@PathParam("checklist_id") int checklistId,
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

    final Either<CompletionStage<Errors>, CompletionStage<Collection<ChecklistItem>>> allItemsResponse =
      checklistItemService.getAll(checklistId, checklistItemFilterMapper.mapFilter(filterDto));
    return this.mapResponse(allItemsResponse, items ->
      Response.status(200).entity(items).build());
  }

  @GET()
  @Path("/{task_id}")
  public CompletionStage<Response> findTaskById(@PathParam("task_id") int taskId,
    @PathParam("checklist_id") int checklistId) {
    final Either<CompletionStage<Errors>, CompletionStage<Optional<ChecklistItem>>> findByIdResponse =
      checklistItemService.findById(checklistId, taskId);
    return this.mapResponse(findByIdResponse, this::createResponse);
  }

  @POST()
  public CompletionStage<Response> saveTask(@NotNull @Valid ChecklistItemDto checklistItemDto,
    @PathParam("checklist_id") Integer checklistId, @Context UriInfo uriInfo) {
    final ChecklistItem checklistItem = checklistItemMapper.map(checklistItemDto);
    final Either<CompletionStage<Errors>, CompletionStage<ChecklistItem>> savedItemResponse =
      checklistItemService.save(checklistId, checklistItem);

    return this.mapResponse(savedItemResponse, savedItem -> {
      final BaseItemDto savedBaseItemDto = checklistItemMapper.map(savedItem);
      return created(savedBaseItemDto, uriInfo);
    });
  }

  @PUT
  @Path("/{id}")
  public CompletionStage<Response> updateTask(@NotNull @Valid ChecklistItemDto checklistItemDto,
    @PathParam("id") Long taskId, @PathParam("checklist_id") int checklistId) {
    checklistItemDto.setId(taskId);
    final ChecklistItem checklistItem = checklistItemMapper.map(checklistItemDto);

    final Either<CompletionStage<Errors>, CompletionStage<ChecklistItem>> itemUpdateResponse =
      checklistItemService.update(checklistId, checklistItem);

    return this.mapResponse(itemUpdateResponse,
      updatedTask -> Response.ok().entity(updatedTask).build());
  }

  @DELETE
  @Path("/{id}")
  public CompletionStage<Response> deleteTask(@PathParam("id") int id,
    @PathParam("checklist_id") int checklistId) {
    final Either<CompletionStage<Errors>, CompletionStage<Void>> deleteResponse =
      checklistItemService.delete(checklistId, id);

    return this.mapResponse(deleteResponse,
      __ -> Response.noContent().build());
  }

  @PATCH
  @Path("/change-order")
  public CompletionStage<Response> changeTaskOrder(ChangeOrderRequest changeOrderRequest,
    @PathParam("checklist_id") Long checklistId) {
    changeOrderRequest.setChecklistId(checklistId);

    return null;
  }

  @POST
  @Path("/save-multiple")
  public CompletionStage<Response> saveMultiple(
    @NotNull @Valid List<ChecklistItemDto> checklistItemDtos,
    @PathParam("checklist_id") int checklistId) {
    final List<ChecklistItem> checklistItems = checklistItemDtos.stream()
      .map(checklistItemMapper::map)
      .toList();

    final Either<CompletionStage<Errors>, CompletionStage<Collection<ChecklistItem>>> savedItemsResponse =
      checklistItemService.saveAll(checklistItems, checklistId);

    return this.mapResponse(savedItemsResponse, savedItems ->
      Response.status(200).entity(savedItems).build());
  }
}
