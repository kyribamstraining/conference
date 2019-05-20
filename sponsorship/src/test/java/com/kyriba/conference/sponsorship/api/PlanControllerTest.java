package com.kyriba.conference.sponsorship.api;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;


/**
 * @author M-ASL
 * @since v1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PlanControllerTest
{
  @Test
  public void registerPlan()
  {
    String id = given()
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .body("{\n" +
            "  \"category\": \"GENERAL\" ,\n" +
            "  \"sponsorEmail\": \"aaa@bbb.by\"\n" +
            "}")
        .when()
        .post("/api/v1/sponsorship/plans")
        .then()
        .statusCode(HttpStatus.SC_OK)
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .extract()
        .jsonPath()
        .get("id");
    Assert.assertNotNull(id);
  }


  @Test
  public void cancelPlan()
  {
    String id = given()
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
        .put("/api/v1/sponsorship/plans/123/cancellation")
        .then()
        .statusCode(HttpStatus.SC_OK)
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .extract()
        .jsonPath()
        .get("id");
    Assert.assertNotNull(id);
  }
}