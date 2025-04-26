/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2025, The ComiXed Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses>
 */

package org.comixedproject.metadata.comicvine.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.metadata.MetadataException;
import org.comixedproject.metadata.comicvine.model.ComicVineGetStoryListResponse;
import org.comixedproject.metadata.model.StoryMetadata;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * <code>ComicVineGetStoriesAction</code> defines an action that loads a list of candidates when
 * scraping a story arc.
 *
 * @author Darryl L. Pierce
 */
@Log4j2
public class ComicVineGetStoriesAction
    extends AbstractComicVineScrapingAction<List<StoryMetadata>> {
  @Getter @Setter private String storyName;
  @Getter @Setter private Integer maxRecords;
  private int page;

  @Override
  public List<StoryMetadata> execute() throws MetadataException {
    this.doCheckSetup();

    this.addFilter(NAME_FILTER, this.storyName);

    this.addField("id");
    this.addField("name");
    this.addField("publisher");
    this.addField("image");

    this.addParameter(RESOURCES_PARAMETER, "volume");
    this.addParameter(QUERY_PARAMETER, this.storyName);
    if (maxRecords > 0) this.addParameter(RESULT_LIMIT_PARAMETER, String.valueOf(this.maxRecords));

    List<StoryMetadata> result = new ArrayList<>();
    boolean done = false;

    while (!done) {
      this.doIncrementPage();

      log.debug(
          "Creating url for: API key=****{} story name={}", this.getMaskedApiKey(), this.storyName);
      final String url = this.createUrl(this.baseUrl, "story_arcs");
      final WebClient client = this.createWebClient(url);
      final Mono<ComicVineGetStoryListResponse> request =
          client.get().uri(url).retrieve().bodyToMono(ComicVineGetStoryListResponse.class);
      ComicVineGetStoryListResponse response = null;

      try {
        response = request.block();
      } catch (Exception error) {
        throw new MetadataException("Failed to get response", error);
      }

      if (response == null) {
        throw new MetadataException("Failed to receive a response");
      }
      log.debug("Received: {} stories", response.getResults().size());

      Integer totalRecords = maxRecords;
      if (totalRecords == 0 || totalRecords >= response.getResults().size())
        totalRecords = response.getResults().size();

      response
          .getResults()
          .subList(0, totalRecords)
          .forEach(
              storyListEntry -> {
                log.trace(
                    "Processing story record: {} name={}",
                    storyListEntry.getReferenceId(),
                    storyListEntry.getName());
                final StoryMetadata entry = new StoryMetadata();
                entry.setReferenceId(storyListEntry.getReferenceId());
                if (Objects.nonNull(storyListEntry.getPublisher())) {
                  entry.setPublisher(storyListEntry.getPublisher().getName());
                }
                entry.setName(storyListEntry.getName());
                entry.setImageUrl(storyListEntry.getImages().get("original_url"));
                result.add(entry);
              });

      done =
          (hitMaxRecordLimit(result))
              || (response.getOffset() + response.getNumberOfPageResults())
                  >= response.getNumberOfTotalResults();

      if (!done) {
        log.trace("Sleeping for {}s", this.getDelay());
        try {
          Thread.sleep(this.getDelay() * 1000L);
        } catch (InterruptedException error) {
          log.error("ComicVine get volumes action interrupted", error);
          throw new RuntimeException(error);
        }
      }
    }

    return result;
  }

  private void doIncrementPage() {
    log.trace("Incremented page value: {}", this.page);
    this.page++;
    if (this.page > 1) {
      log.trace("Setting page: {}", this.page);
      this.addParameter(PAGE_PARAMETER, String.valueOf(this.page));
    }
  }

  private void doCheckSetup() throws MetadataException {
    if (!StringUtils.hasLength(this.getApiKey())) throw new MetadataException("Missing API key");
    if (!StringUtils.hasLength(this.storyName)) throw new MetadataException("Missing story name");
    if (maxRecords == null) throw new MetadataException("Missing maximum records");
  }

  private boolean hitMaxRecordLimit(final List<StoryMetadata> records) {
    return this.maxRecords > 0 && records.size() == this.maxRecords;
  }
}
