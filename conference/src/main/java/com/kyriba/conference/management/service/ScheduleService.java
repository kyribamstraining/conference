package com.kyriba.conference.management.service;

import com.kyriba.conference.management.domain.dto.PresentationRequest;
import com.kyriba.conference.management.domain.dto.PresentationResponse;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;


public interface ScheduleService
{
  List<PresentationResponse> getSchedule();

  long addPresentation(@Valid PresentationRequest presentation);

  Optional<PresentationResponse> getPresentation(@Valid @Positive long id);

  void updatePresentation(@Valid @Positive long id, @Valid PresentationRequest presentation);

  void deletePresentation(@Valid @Positive long id);
}
