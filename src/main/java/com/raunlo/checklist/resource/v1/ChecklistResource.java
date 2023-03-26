package com.raunlo.checklist.resource.v1;

import com.raunlo.checklist.core.entity.Checklist;
import com.raunlo.checklist.core.entity.ChecklistBuilder;
import com.raunlo.checklist.core.service.ChecklistService;
import com.raunlo.checklist.resource.BaseResource;
import com.raunlo.checklist.resource.dto.ChecklistDto;
import com.raunlo.checklist.resource.mapper.ChecklistDtoMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.concurrent.CompletionStage;

@Path("api/v1/checklist")
@ApplicationScoped()
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChecklistResource extends BaseResource {

  private final ChecklistService checklistService;
  private final ChecklistDtoMapper checklistDtoMapper;

  @Inject()
  public ChecklistResource(final ChecklistService checklistService,
      final ChecklistDtoMapper checklistDtoMapper) {

    this.checklistService = checklistService;
    this.checklistDtoMapper = checklistDtoMapper;
  }

  @GET()
  @Path("/{id}")
  public CompletionStage<Response> getChecklistById(@PathParam("id") Long id) {

    final var response = checklistService.findById(id);
    return mapResponse(response, entity ->
        this.ok(entity.map(checklistDtoMapper::map)));
  }

  @GET()
  public CompletionStage<Response> getAllCheckLists() {

    var response = checklistService.getAll();
    return mapResponse(response,
        entity -> this.ok(checklistDtoMapper.map(entity)));
  }

  @POST()
  public CompletionStage<Response> saveChecklist(@NotNull @Valid ChecklistDto checklistDto,
      @Context UriInfo uriInfo) {

    var checklist = checklistDtoMapper.map(checklistDto);
    var response = checklistService.save(checklist);
    return mapResponse(response,
        entity -> this.created(uriInfo, checklistDtoMapper.map(entity))
    );
  }

  @PATCH()
  @Path("/{id}")
  public CompletionStage<Response> updateTask(@NotNull @Valid Checklist checklist,
      @PathParam("id") Long id) {

    var response = checklistService.update(
        ChecklistBuilder.builder(checklist)
            .id(id)
            .build());
    return mapResponse(response,
        entity -> this.ok(checklistDtoMapper.map(entity)));
  }

  @DELETE
  @Path("/{id}")
  public CompletionStage<Response> deleteChecklist(@PathParam("id") Long id) {
    var response = checklistService.delete(id);
    return mapResponse(response, (__) -> this.noContent());
  }
}
