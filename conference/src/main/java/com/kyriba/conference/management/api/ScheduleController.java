package com.kyriba.conference.management.api;


import com.kyriba.conference.management.domain.dto.PresentationRequest;
import com.kyriba.conference.management.domain.dto.PresentationResponse;
import com.kyriba.conference.management.domain.exception.InvalidPresentationTime;
import com.kyriba.conference.management.domain.exception.LinkedEntityNotFound;
import com.kyriba.conference.management.domain.exception.PresentationTimeIntersectionException;
import com.kyriba.conference.management.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;


@Api(value = "Conference schedule management endpoint")
@RestController
@RequestMapping("/api/v1/schedule")
@SuppressWarnings("unused")
@AllArgsConstructor
public class ScheduleController
{
  private final ScheduleService scheduleService;


  @ApiOperation(value = "Show conference schedule")
  @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  ScheduleResponse showSchedule()
  {
    return new ScheduleResponse(scheduleService.getSchedule());
  }


  @ApiOperation(value = "Add presentation in conference schedule")
  @PostMapping(value = "/presentations", produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
  long addPresentation(
      @ApiParam(value = "Presentation create object", required = true) @RequestBody PresentationRequest presentationRequest)
  {
    return scheduleService.addPresentation(presentationRequest);
  }


  @ApiOperation(value = "View presentation information")
  @GetMapping(value = "/presentations/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
  PresentationResponse getPresentation(
      @ApiParam(value = "Presentation identity", required = true) @PathVariable long id)
  {
    return scheduleService.getPresentation(id)
        .orElseThrow(() -> new ResourceNotFoundException("Presentation not found."));
  }


  @ApiOperation(value = "Remove presentation from conference schedule")
  @DeleteMapping(value = "/presentations/{id}", consumes = APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(value = NO_CONTENT)
  void removePresentation(
      @ApiParam(value = "Presentation identity", required = true) @PathVariable long id)
  {
    scheduleService.deletePresentation(id);
  }


  @ApiOperation(value = "Change presentations parameters")
  @PutMapping(value = "/presentations/{id}", consumes = APPLICATION_JSON_UTF8_VALUE)
  void updatePresentation(
      @ApiParam(value = "Presentation identity", required = true) @PathVariable long id,
      @ApiParam(value = "Presentation change object", required = true) @RequestBody PresentationRequest presentationRequest)
  {
    scheduleService.updatePresentation(id, presentationRequest);
  }


  @ExceptionHandler({ LinkedEntityNotFound.class, InvalidPresentationTime.class,
      PresentationTimeIntersectionException.class })
  void handleException(HttpServletResponse response, RuntimeException e) throws IOException
  {
    response.sendError(BAD_REQUEST.value(), e.getMessage());
  }


  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(value = CONFLICT, reason = "The same Topic already exists.")
  void handleDuplicateKeyException()
  {
  }


  @ApiModel(description = "Conference schedule response model")
  @Value
  static class ScheduleResponse
  {
    @ApiModelProperty(value = "List of presentation")
    private List<PresentationResponse> presentations;
  }
}
