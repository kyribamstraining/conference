package com.kyriba.conference.api;


import com.kyriba.conference.api.dto.PresentationRequest;
import com.kyriba.conference.api.dto.PresentationResponse;
import com.kyriba.conference.api.dto.ScheduleResponse;
import com.kyriba.conference.api.dto.TopicRequestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.time.LocalTime.of;
import static java.util.Arrays.asList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;


@Api(value = "Conference schedule management endpoint")
@RestController
@RequestMapping("/api/v1/schedule")
public class ScheduleController
{

  @ApiOperation(value = "Show conference schedule", response = ScheduleResponse.class)
  @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  ScheduleResponse showSchedule()
  {
    return ScheduleResponse.builder().presentations(buildPresentations()).build();
  }


  @ApiOperation(value = "Add presentation in conference schedule", response = PresentationResponse.class)
  @PostMapping(value = "/presentations", produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
  PresentationResponse addPresentation(
      @ApiParam(value = "Presentation create object", required = true) PresentationRequest presentation)
  {
    return PresentationResponse.builder()
        .id(55L)
        .build();
  }


  @ApiOperation(value = "View presentation information", response = PresentationResponse.class)
  @GetMapping(value = "/presentations/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
  PresentationResponse getPresentation(
      @ApiParam(value = "Presentation identity", required = true) @PathVariable String id)
  {
    return PresentationResponse.builder()
        .id(Long.valueOf(id))
        .build();
  }


  @ApiOperation(value = "Remove presentation from conference schedule", response = PresentationResponse.class)
  @DeleteMapping(value = "/presentations/{id}", consumes = APPLICATION_JSON_UTF8_VALUE)
  PresentationResponse removePresentation(
      @ApiParam(value = "Presentation identity", required = true) @PathVariable String id)
  {
    return PresentationResponse.builder()
        .id(55L)
        .build();
  }


  @ApiOperation(value = "Change presentations parameters", response = PresentationResponse.class)
  @PatchMapping(value = "/presentations/{id}", produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
  PresentationResponse updatePresentation(
      @ApiParam(value = "Presentation identity", required = true) @PathVariable String id,
      @ApiParam(value = "Presentation change object", required = true) PresentationRequest presentation)
  {
    // update partially - only provided changes
    return PresentationResponse.builder()
        .id(55L)
        .build();
  }


  private List<PresentationResponse> buildPresentations()
  {
    return asList(
        PresentationResponse.builder()
            .hall(101L)
            .topic(new TopicRequestResponse("Spring Data REST", "Andy Wilkinson"))
            .startTime(of(10, 00))
            .endTime(of(11, 15))
            .build(),
        PresentationResponse.builder()
            .hall(101L)
            .topic(new TopicRequestResponse("Microservices in practice", "Mikalai Alimenkou"))
            .startTime(of(11, 45))
            .endTime(of(15, 00))
            .build(),
        PresentationResponse.builder()
            .hall(122L)
            .topic(new TopicRequestResponse("All about Spring workshop", "Ivan Ivanou"))
            .startTime(of(10, 0))
            .endTime(of(15, 15))
            .build()
    );
  }
}
