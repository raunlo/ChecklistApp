package com.raunlo.checklist.resource.v1;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.ExistingItem;
import com.raunlo.checklist.core.entity.error.Errors;
import com.raunlo.checklist.core.service.ExistingItemsService;
import com.raunlo.checklist.resource.BaseResource;
import com.raunlo.checklist.resource.dto.item.BaseItemDto;
import com.raunlo.checklist.resource.dto.item.ExistingItemDto;
import com.raunlo.checklist.resource.mapper.ExistingItemMapper;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Path("api/v1/list/{list_id}/item/existing")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public final class ExistingItemResource extends BaseResource {

  private final ExistingItemsService existingItemsService;
  private final ExistingItemMapper existingItemMapper;

  @Inject()
  public ExistingItemResource(final ExistingItemsService existingItemsService,
    ExistingItemMapper existingItemMapper) {
    this.existingItemsService = existingItemsService;
    this.existingItemMapper = existingItemMapper;
  }

  @GET()
  public CompletionStage<Response> getTasks(@PathParam("list_id") Long checklistId) {
    final Either<CompletionStage<Errors>, CompletionStage<Collection<ExistingItem>>> allItems =
      existingItemsService.getAll(checklistId);

    return this.mapResponse(allItems, items -> {
      final List<ExistingItemDto> itemsDto = items.stream()
        .map(existingItemMapper::map)
        .collect(Collectors.toList());

      return Response.status(Status.OK).entity(itemsDto).build();
    });
  }

  @GET()
  @Path("/{task_id}")
  public CompletionStage<Response> findTaskById(@PathParam("task_id") int taskId,
    @PathParam("list_id") long checklistId) {
    final Either<CompletionStage<Errors>, CompletionStage<Optional<ExistingItem>>> findByIdResponse =
      existingItemsService.findById(checklistId, taskId);

    return this.mapResponse(findByIdResponse, this::createResponse);
  }

  @POST()
  public CompletionStage<Response> saveTask(@NotNull @Valid ExistingItemDto existingItemDto,
    @NotNull @PathParam("list_id") Long checklistId, @Context UriInfo uriInfo) {
    final ExistingItem existingItem = existingItemMapper.map(existingItemDto);
    final Either<CompletionStage<Errors>, CompletionStage<ExistingItem>> savedItemResponse =
      existingItemsService.save(checklistId, existingItem);

    return this.mapResponse(savedItemResponse, savedItem -> {
      final ExistingItemDto itemDto = existingItemMapper.map(savedItem);
      return this.created(itemDto, uriInfo);
    });
  }

  @PUT
  @Path("/{id}")
  public CompletionStage<Response> updateTask(@NotNull @Valid ExistingItemDto existingItemDto,
    @PathParam("id") long taskId, @PathParam("list_id") Long checklistId) {
    final ExistingItem existingItem = existingItemMapper.map(existingItemDto);
    existingItem.setId(taskId);

    final Either<CompletionStage<Errors>, CompletionStage<ExistingItem>> updateResponse =
      existingItemsService.update(checklistId, existingItem);

    return this.mapResponse(updateResponse, updatedItem -> {
      final BaseItemDto savedBaseItemDto = existingItemMapper.map(updatedItem);
      return Response.status(Status.OK).entity(savedBaseItemDto).build();
    });
  }

  @DELETE
  @Path("/{id}")
  public CompletionStage<Response> deleteTask(@PathParam("id") int id,
    @PathParam("list_id") Long checklistId) {
    final Either<CompletionStage<Errors>, CompletionStage<Void>> deleteResponse =
      existingItemsService.delete(checklistId, id);

    return this.mapResponse(deleteResponse,
      __ -> Response.noContent().build());
  }

  @PATCH
  @Path("/change-order")
  public CompletionStage<Response> changeTaskOrder(
    @NotNull @Valid ChangeOrderRequest changeOrderRequest,
    @PathParam("list_id") Long checklistId) {
    changeOrderRequest.setChecklistId(checklistId);

    final Either<CompletionStage<Errors>, CompletionStage<Void>> changeOrderResponse =
      existingItemsService.changeOrder(changeOrderRequest);

    return this.mapResponse(changeOrderResponse,
      __ -> Response.status(200).build());
  }

  @POST
  @Path("/save-multiple")
  public CompletionStage<Response> saveMultiple(
    @NotNull @Valid List<ExistingItemDto> existingItemDtos,
    @PathParam("list_id") Long checklistId) {
    final List<ExistingItem> existingItems = existingItemDtos.stream()
      .map(existingItemMapper::map)
      .toList();

    final Either<CompletionStage<Errors>, CompletionStage<Collection<ExistingItem>>> itemsSavedResponse =
      existingItemsService.saveAll(existingItems, checklistId);

    return this.mapResponse(itemsSavedResponse, savedItems ->
      Response.status(200).entity(savedItems).build());
  }
}
