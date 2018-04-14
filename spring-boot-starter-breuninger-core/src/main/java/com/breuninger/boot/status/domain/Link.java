package com.breuninger.boot.status.domain;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor(access = PRIVATE)
@EqualsAndHashCode
@ToString
@JsonInclude(Include.NON_NULL)
public class Link {

  public final String href;
  public final String rel;
  public final String title;

  public static Link link(final String rel, final String href, final String title) {
    return new Link(href, rel, title);
  }
}
