package com.kyriba.conference.sponsorship.service;

import com.kyriba.conference.sponsorship.dao.SponsorRepository;
import com.kyriba.conference.sponsorship.domain.Sponsor;
import com.kyriba.conference.sponsorship.domain.dto.SponsorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;


/**
 * @author M-ASL
 * @since v1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SponsorServiceImpl implements SponsorService
{
  private final SponsorRepository sponsorRepository;
  private final EmailClient emailClient;


  @Override
  public long createSponsor(String name, String email)
  {
    Sponsor sponsor = new Sponsor();
    sponsor.setName(name);
    sponsor.setEmail(email);
    sendEmailNotification(sponsor);
    return sponsorRepository.save(sponsor).getId();
  }


  @Override
  public void sendEmailNotification(Sponsor sponsor)
  {
    try {
      emailClient.sendNotification(sponsor.toEmailMessage());
    }
    catch (Exception e) {
      //todo it is workaround, what behavior is expected when the other service is not available?
      e.printStackTrace();
    }
  }


  @Override
  public Optional<SponsorDto> readSponsor(long id)
  {
    return sponsorRepository.findById(id).map(Sponsor::toDto);
  }
}
