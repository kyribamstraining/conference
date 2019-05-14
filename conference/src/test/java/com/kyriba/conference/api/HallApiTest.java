package com.kyriba.conference.api;


import com.kyriba.conference.api.dto.HallRequest;
import com.kyriba.conference.api.dto.HallResponse;
import com.kyriba.conference.domain.Hall;
import com.kyriba.conference.service.HallServiceImpl;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class HallApiTest
{
  @Rule
  public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

  private RequestSpecification documentationSpec;


  @Before
  public void setUp()
  {
    documentationSpec = new RequestSpecBuilder()
        .addFilter(documentationConfiguration(restDocumentation)).build();
  }


  @MockBean
  HallServiceImpl hallService;


  @Test
  public void getAllHalls()
  {
    List<Hall> existingHalls = asList(
        new Hall(11L).withName("test11").withPlaces(10),
        new Hall(12L).withName("test12").withPlaces(12));
    doReturn(existingHalls).when(hallService).findAll();

    HallResponse[] halls = given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/halls/getAll"))

        .when()
        .get("/api/v1/halls")

        .then()
        .statusCode(HttpStatus.SC_OK)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .extract().body().as(HallResponse[].class);

    assertNotNull(halls);
    assertEquals(2, halls.length);
    assertEquals(11L, halls[0].getId().longValue());
    assertEquals("test11", halls[0].getName());
    assertEquals(10, halls[0].getPlaces().intValue());
    assertEquals(12L, halls[1].getId().longValue());
    assertEquals("test12", halls[1].getName());
    assertEquals(12, halls[1].getPlaces().intValue());
  }


  @Test
  public void addHall()
  {
    Long hallId = 1L;
    String hallName = "Audience 01";
    int hallPlaces = 40;
    doReturn(new Hall(hallId).withName(hallName).withPlaces(hallPlaces))
        .when(hallService).createHall(any(HallRequest.class));

    HallResponse hall = given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/halls/add"))
        .body("{\n" +
            "  \"hallName\": \"" + hallName + "\",\n" +
            "  \"places\": \"" + hallPlaces + "\"\n" +
            "}")

        .when()
        .post("/api/v1/halls")

        .then()
        .statusCode(HttpStatus.SC_OK)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .extract().body().as(HallResponse.class);

    assertEquals(hallId, hall.getId());
    assertEquals(hallName, hall.getName());
    assertEquals(hallPlaces, hall.getPlaces().intValue());
  }


  @Test
  public void updateHallPlaceCount()
  {
    Long hallId = 1011L;
    String hallName = "Audience 101";
    int hallPlaces = 45;
    doReturn(new Hall(hallId).withName(hallName).withPlaces(hallPlaces))
        .when(hallService).updateHall(eq(hallId), any(HallRequest.class));

    Long id = given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/halls/update"))
        .body("{\n" +
            "  \"hallName\": \"" + hallName + "\",\n" +
            "  \"places\": \"" + hallPlaces + "\"\n" +
            "}")

        .when()
        .put("/api/v1/halls/" + hallId)

        .then()
        .statusCode(HttpStatus.SC_OK)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .extract()
        .jsonPath().getLong("id");

    assertEquals(id, hallId);
  }


  @Test
  public void removeHall()
  {
    given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/halls/delete"))

        .when()
        .delete("/api/v1/halls/1101")
        .then()
        .statusCode(HttpStatus.SC_OK);
  }

}
