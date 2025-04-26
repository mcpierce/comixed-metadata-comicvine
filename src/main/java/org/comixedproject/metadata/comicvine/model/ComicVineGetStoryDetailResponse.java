package org.comixedproject.metadata.comicvine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ComicVineGetStoryDetailResponse extends AbstractComicVineQueryResponse {
  @JsonProperty("results")
  @Getter
  private ComicVineStoryDetailResponse results;
}
