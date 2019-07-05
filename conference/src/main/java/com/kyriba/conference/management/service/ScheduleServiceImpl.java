package com.kyriba.conference.management.service;

import com.kyriba.conference.management.domain.dto.PresentationRequest;
import com.kyriba.conference.management.domain.dto.PresentationResponse;
import com.kyriba.conference.management.dao.HallRepository;
import com.kyriba.conference.management.dao.PresentationRepository;
import com.kyriba.conference.management.domain.Hall;
import com.kyriba.conference.management.domain.Presentation;
import com.kyriba.conference.management.domain.exception.LinkedEntityNotFound;
import com.kyriba.conference.management.domain.exception.PresentationTimeIntersectionException;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;


@Service
@Transactional
@AllArgsConstructor
public class ScheduleServiceImpl implements ScheduleService
{
  private static final String HALL_NOT_FOUND = "Hall not found.";

  private final PresentationRepository presentationRepository;

  private final HallRepository hallRepository;


  @Override
  public List<PresentationResponse> getSchedule()
  {
    return presentationRepository.findAll().stream()
        .map(Presentation::toDto)
        .collect(Collectors.toList());
  }


  @Override
  public long addPresentation(PresentationRequest presentationRequest)
  {
    Presentation presentation = new Presentation(presentationRequest);
    setPresentationHall(presentation, presentationRequest.getHall());
    validateTimeIntersection(presentation);

    return presentationRepository.save(presentation).getId();
  }


  @Override
  public Optional<PresentationResponse> getPresentation(long id)
  {
    return presentationRepository.findById(id)
        .map(Presentation::toDto);
  }


  @Transactional
  @Override
  public void updatePresentation(long id, PresentationRequest presentationRequest)
  {
    Presentation presentation = presentationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Presentation not found."));
    presentation.update(presentationRequest);
    setPresentationHall(presentation, presentationRequest.getHall());
    validateTimeIntersection(presentation);

    presentationRepository.save(presentation);
  }


  private void setPresentationHall(Presentation presentation, long hallId)
  {
    Hall hall = hallRepository.findById(hallId)
        .orElseThrow(() -> new LinkedEntityNotFound(HALL_NOT_FOUND));
    presentation.setHall(hall);
  }


  @Override
  public void deletePresentation(long id)
  {
    presentationRepository.deleteById(id);
  }


  private void validateTimeIntersection(Presentation presentation)
  {
    final LocalTime startTime = presentation.getStartTime();
    final LocalTime endTime = presentation.getEndTime();

    try (Stream<Presentation> intersections = presentationRepository
        .findByHallAndStartTimeBetweenOrEndTimeBetween(presentation.getHall(), startTime, endTime, startTime, endTime)) {

      Predicate<Presentation> anotherPresentation = pr -> !pr.getId().equals(presentation.getId());
      intersections
          .filter(anotherPresentation)
          .findAny()
          .ifPresent(pr -> {
                throw new PresentationTimeIntersectionException(
                    format("Presentation time intersection: wanted %s - %s, already reserved %s - %s.",
                        presentation.getStartTime(), presentation.getEndTime(), pr.getStartTime(), pr.getEndTime()));
              }
          );

    }
  }
}
