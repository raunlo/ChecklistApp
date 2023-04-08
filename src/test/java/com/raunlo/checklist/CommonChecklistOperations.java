package com.raunlo.checklist;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.raunlo.checklist.core.entity.ChangeOrderRequest;
import com.raunlo.checklist.core.entity.TaskPredefinedFilter;
import com.raunlo.checklist.resource.dto.ChecklistDto;
import com.raunlo.checklist.resource.dto.ChecklistItemDto;
import com.raunlo.checklist.resource.v1.ChecklistResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.net.URL;
import java.util.Collection;


public class CommonChecklistOperations {

  private static final String CHECKLIST_ITEM_PATH = "/{checklist_id}/task/";

  @TestHTTPEndpoint(ChecklistResource.class)
  @TestHTTPResource
  URL checklistResourceUrl;


  public ChecklistDto createChecklist(ChecklistDto checklistDto) {
    var response = given()
        .contentType(ContentType.JSON)
        .body(checklistDto)
        .when().post(checklistResourceUrl)
        .then()
        .statusCode(201).extract().response();

    var savedChecklist = response.as(ChecklistDto.class);
    assertThat(response.header("Location"))
        .isEqualTo(checklistResourceUrl + "/" + savedChecklist.id());

    assertThat(savedChecklist.id()).isNotNull();
    assertThat(savedChecklist.name()).isEqualTo(checklistDto.name());
    assertThat(savedChecklist.checklistItemDtos()).matches(tasks -> tasks.size() == 0);

    return savedChecklist;
  }


  public Response createChecklistItem(long checklistId,
      ChecklistItemDto checklistItemDto) {
    return given()
        .contentType(ContentType.JSON)
        .body(checklistItemDto)
        .when().post(checklistResourceUrl + "/" + checklistId + "/task")
        .then().extract().response();
  }

  public ChecklistItemDto successfullyCreatesChecklistItem(long checklistId,
      ChecklistItemDto checklistItemDto) {
    var response = createChecklistItem(checklistId, checklistItemDto);

    var item = response.as(ChecklistItemDto.class);
    assertThat(item.id()).isNotNull();
    assertThat(item.name()).isEqualTo(checklistItemDto.name());
    assertThat(item.completed()).isEqualTo(checklistItemDto.completed());
    assertThat(response.getHeader("Location")).isEqualTo(
        checklistResourceUrl + "/" + checklistId + "/task/" + item.id());

    return item;
  }


  public Response getTask(long checklistId, long taskId) {
    return given().pathParam("checklist_id", checklistId)
        .contentType(ContentType.JSON)
        .when()
        .get(checklistResourceUrl + CHECKLIST_ITEM_PATH + taskId)
        .then().extract().response();
  }

  public void deleteTask(long checklistId, long taskId) {
    given().pathParam("checklist_id", checklistId)
        .contentType(ContentType.JSON)
        .when()
        .delete(checklistResourceUrl + CHECKLIST_ITEM_PATH + taskId)
        .then().statusCode(204).extract().response();
  }

  public Collection<ChecklistItemDto> getAllChecklistItems(long checklistId,
      TaskPredefinedFilter taskPredefinedFilter) {
    var request = given().contentType(ContentType.JSON).pathParam("checklist_id", checklistId);
    if (taskPredefinedFilter != null) {
      request.queryParam("filterType",
          taskPredefinedFilter.name().toLowerCase());
    }

    var checklistItems = request.when()
        .get(checklistResourceUrl + CHECKLIST_ITEM_PATH)
        .then().statusCode(200)
        .extract().body().jsonPath().getList(".", ChecklistItemDto.class);

    return checklistItems;
  }

  public ChecklistItemDto updateTask(long checklistId, ChecklistItemDto checklistItemDto) {
    var updatedChecklistItem = given().pathParam("checklist_id", checklistId)
        .contentType(ContentType.JSON)
        .when().body(checklistItemDto)
        .put(checklistResourceUrl + CHECKLIST_ITEM_PATH + checklistItemDto.id())
        .then().statusCode(200).extract().as(ChecklistItemDto.class);

    return updatedChecklistItem;
  }

  public void updateTaskOrder(final ChangeOrderRequest changeOrderRequest) {
     given()
        .pathParam("checklist_id", changeOrderRequest.checklistId())
        .contentType(ContentType.JSON)
        .body(changeOrderRequest)
        .when()
        .patch(checklistResourceUrl + CHECKLIST_ITEM_PATH + "change-order")
         .then().statusCode(204);
  }

  public void saveMultipleChecklistItems(long checklistId, Collection<ChecklistItemDto> itemDtos) {
    given()
        .pathParam("checklist_id", checklistId)
        .contentType(ContentType.JSON)
        .body(itemDtos)
        .when()
        .post(checklistResourceUrl + CHECKLIST_ITEM_PATH + "save-multiple")
        .then().statusCode(201)
        .extract().body().jsonPath().getList(".", ChecklistItemDto.class);
  }
}

