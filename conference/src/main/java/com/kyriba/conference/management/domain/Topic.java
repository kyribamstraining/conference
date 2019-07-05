package com.kyriba.conference.management.domain;


import com.kyriba.conference.management.domain.dto.TopicDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;


@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Topic
{
  private String title;
  private String author;


  public Topic(TopicDto dto)
  {
    title = dto.getTitle();
    author = dto.getAuthor();
  }
}
