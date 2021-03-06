package com.kyriba.conference.management.api;

import com.kyriba.conference.management.domain.dto.PresentationResponse;
import com.kyriba.conference.management.domain.dto.TopicDto;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalTime;

import static com.kyriba.conference.management.api.TestHelper.getPresentationJson;
import static io.restassured.RestAssured.given;
import static java.time.LocalTime.of;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("apitest")
public class ScheduleApiTest
{
  @Rule
  public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

  private RequestSpecification documentationSpec;

  @LocalServerPort
  int port;

  @Before
  public void setUp()
  {
    RestAssured.port = port;
    documentationSpec = new RequestSpecBuilder()
        .addFilter(documentationConfiguration(restDocumentation)).build();
  }


  @Test
  public void showSchedule()
  {
    ScheduleController.ScheduleResponse schedule = given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/schedule/view"))

        .when()
        .get("/api/v1/schedule")

        .then()
        .statusCode(SC_OK)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .extract().body().as(ScheduleController.ScheduleResponse.class);

    assertThat(schedule.getPresentations()).hasSize(3);
    assertThat(schedule.getPresentations())
        .extracting("topic").isNotNull()
        .extracting("title")
        .containsOnly("Spring Data REST", "Microservices in practice", "All about Spring workshop");
  }


  @Test
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:afterAddPresentation.sql")
  public void addPresentationInSchedule()
  {
    final long hallId = 11;
    final String topicTitle = "Spring Data REST part 2";
    final String topicAuthor = "Andy Wilkinson";
    final LocalTime startTime = of(9, 0);
    final LocalTime endTime = of(9, 50);

    Long presentationId = given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/schedule/presentations/add"))
        .body(getPresentationJson(hallId, topicTitle, topicAuthor, startTime, endTime))

        .when()
        .post("/api/v1/schedule/presentations")

        .then()
        .statusCode(SC_OK)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .extract().as(ScheduleController.PresentationCreatedResponse.class)
        .getId();

    // check that Presentation is added
    PresentationResponse presentation = given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .when()
        .get("/api/v1/schedule/presentations/" + presentationId)

        .then()
        .statusCode(SC_OK)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .extract().body().as(PresentationResponse.class);

    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(presentation.getHall()).isEqualTo(hallId);
      softly.assertThat(presentation.getStartTime()).isEqualTo(startTime);
      softly.assertThat(presentation.getEndTime()).isEqualTo(endTime);
      softly.assertThat(presentation.getTopic()).isEqualTo(new TopicDto(topicTitle, topicAuthor));
    });
  }

  @Test
  public void addPresentationWithNonexistentHall()
  {
    given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/schedule/presentations/addWIthNonexistentHall"))
        .body(getPresentationJson(1111L, "Spring Data REST", "Andy Wilkinson", of(10, 0), of(11, 15)))

        .when()
        .post("/api/v1/schedule/presentations")

        .then()
        .statusCode(SC_BAD_REQUEST)
        .body("message", equalTo("Hall not found."));
  }


  @Test
  public void viewPresentationInfo()
  {
    PresentationResponse presentation = given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/schedule/presentations/get"))

        .when()
        .get("/api/v1/schedule/presentations/1001")

        .then()
        .statusCode(SC_OK)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .extract().body().as(PresentationResponse.class);

    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(presentation.getHall()).isEqualTo(11);
      softly.assertThat(presentation.getStartTime()).isEqualTo(of(10, 0));
      softly.assertThat(presentation.getEndTime()).isEqualTo(of(11, 15));
      softly.assertThat(presentation.getTopic()).isEqualTo(new TopicDto("Spring Data REST", "Andy Wilkinson"));
    });
  }


  @Test
  public void viewInvalidPresentationId()
  {
    given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .when()
        .get("/api/v1/schedule/presentations/-12")

        .then()
        .statusCode(SC_BAD_REQUEST);
  }


  @Test
  @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:afterRemovePresentationFromSchedule.sql")
  public void removePresentationFromSchedule()
  {
    given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/schedule/presentations/delete"))

        .when()
        .delete("/api/v1/schedule/presentations/1001")
        .then()
        .statusCode(SC_NO_CONTENT);

    // check that Presentation is removed
    given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .when()
        .get("/api/v1/schedule/presentations/1001")

        .then()
        .statusCode(SC_NOT_FOUND);
  }


  @Test
  @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "classpath:afterChangePresentationTime.sql")
  public void changePresentationTime()
  {
    final LocalTime startTime = of(17, 15);
    final LocalTime endTime = of(17, 55);

    given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/schedule/presentations/updateTime"))
        .body(getPresentationJson(11L, "Spring Data REST", "Andy Wilkinson", startTime, endTime))

        .when()
        .put("/api/v1/schedule/presentations/1001")

        .then()
        .statusCode(SC_OK);

    // check that Hall is updated
    PresentationResponse presentation = given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .when()
        .get("/api/v1/schedule/presentations/1001")

        .then()
        .statusCode(SC_OK)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .extract().body().as(PresentationResponse.class);

    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(presentation.getStartTime()).isEqualTo(startTime);
      softly.assertThat(presentation.getEndTime()).isEqualTo(endTime);
    });
  }


  @Test
  public void changeNonexistentPresentation()
  {
    given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/schedule/presentations/updateNonexistent"))
        .body(getPresentationJson(11L, "Spring Data REST", "Andy Wilkinson", of(10, 15), of(11, 55)))

        .when()
        .put("/api/v1/schedule/presentations/555")

        .then()
        .statusCode(SC_NOT_FOUND)
        .body("message", equalTo("Presentation not found."));
  }


  @Test
  public void rearrangePresentationToNonexistentHall()
  {
    given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)
        .filter(document("api/v1/schedule/presentations/updateToNonexistentHall"))
        .body(getPresentationJson(10113242L, "Spring Data REST", "Andy Wilkinson", of(10, 15), of(11, 55)))

        .when()
        .put("/api/v1/schedule/presentations/1001")

        .then()
        .statusCode(SC_BAD_REQUEST)
        .body("message", equalTo("Hall not found."));
  }


  @Test
  public void removeNonexistentPresentationFromSchedule()
  {
    given(documentationSpec)
        .contentType(APPLICATION_JSON_UTF8_VALUE)

        .when()
        .delete("/api/v1/schedule/presentations/1001123112")
        .then()
        .statusCode(SC_NO_CONTENT);
  }
}
