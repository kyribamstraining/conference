package com.kyriba.conference.management.service;

import com.kyriba.conference.management.dao.HallRepository;
import com.kyriba.conference.management.domain.Hall;
import com.kyriba.conference.management.domain.dto.HallRequest;
import com.kyriba.conference.management.domain.dto.HallResponse;
import com.kyriba.conference.management.domain.exception.EntityNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class HallServiceImplTest
{
  @Mock
  private HallRepository hallRepository;

  @InjectMocks
  private HallServiceImpl hallService;

  @Rule
  public ExpectedException thrown = ExpectedException.none();


  @Test
  public void findHall()
  {
    final String hallName = "Hall 1";
    final int places = 10;
    final long id = 22L;
    Hall hall = new Hall(hallName, places);
    doReturn(Optional.of(hall)).when(hallRepository).findById(eq(id));

    HallResponse result = hallService.findHall(id);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(hallName);
    assertThat(result.getPlaces()).isEqualTo(places);
  }


  @Test
  public void ifHallNotFoundThrowException()
  {
    final long id = 22L;
    doReturn(empty()).when(hallRepository).findById(id);

    thrown.expect(EntityNotFoundException.class);
    thrown.expectMessage("Hall not found.");

    hallService.findHall(id);
  }


  @Test
  public void findAllHalls()
  {
    Hall hall1 = new Hall("Hall 1", 11);
    Hall hall2 = new Hall("Hall 2", 12);
    doReturn(asList(hall1, hall2)).when(hallRepository).findAll();

    List<HallResponse> result = hallService.findAllHalls();

    assertThat(result).isNotNull()
        .containsExactlyInAnyOrder(new HallResponse(hall1), new HallResponse(hall2));
  }


  @Test
  public void emptyListIfHallsNotFound()
  {
    doReturn(emptyList()).when(hallRepository).findAll();

    List<HallResponse> result = hallService.findAllHalls();

    assertThat(result).isNotNull().isEmpty();
  }


  @Test
  public void createHall()
  {
    final long id = 22L;
    HallRequest hallRequest = new HallRequest();
    hallRequest.setName("h1");
    hallRequest.setPlaces(11);

    Hall hallToCreate = new Hall(hallRequest);
    Hall createdHall = new Hall(hallRequest);
    createdHall.setId(id);

    doReturn(createdHall).when(hallRepository).save(any(Hall.class));

    long createdHallId = hallService.createHall(hallRequest);

    assertThat(createdHallId).isEqualTo(id);
    verify(hallRepository).save(refEq(hallToCreate));
  }


  @Test
  public void updateHall()
  {
    final long id = 22L;
    Hall storedHall = new Hall("Stored Hall", 11);
    doReturn(Optional.of(storedHall)).when(hallRepository).findById(eq(id));

    HallRequest hallRequest = new HallRequest();
    hallRequest.setName("h1");
    hallRequest.setPlaces(11);
    Hall updatedHall = new Hall(hallRequest);

    hallService.updateHall(id, hallRequest);

    verify(hallRepository).save(refEq(updatedHall));
  }


  @Test
  public void updateNonexistentHallThrowException()
  {
    final long id = 22L;
    doReturn(empty()).when(hallRepository).findById(eq(id));

    thrown.expect(EntityNotFoundException.class);
    thrown.expectMessage("Hall not found.");

    hallService.updateHall(id, new HallRequest());
  }


  @Test
  public void removeHall()
  {
    final long id = 22L;

    hallService.removeHall(id);

    verify(hallRepository).deleteById(eq(id));
  }
}