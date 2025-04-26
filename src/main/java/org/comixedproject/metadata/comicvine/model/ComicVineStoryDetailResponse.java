package org.comixedproject.metadata.comicvine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

public class ComicVineStoryDetailResponse {
  @JsonProperty("id")
  @Getter
  private String id;

  @JsonProperty("publisher")
  @Getter
  private ComicVinePublisher publisher;

  @JsonProperty("description")
  @Getter
  private String description;

  @JsonProperty("issues")
  @Getter
  private List<ComicVineIssue> issues;
}
