/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2022, The ComiXed Project
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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.metadata.MetadataException;
import org.comixedproject.metadata.comicvine.model.ComicVineGetAllIssuesQueryResponse;
import org.comixedproject.metadata.comicvine.model.ComicVineIssue;
import org.comixedproject.metadata.model.IssueDetailsMetadata;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * <code>ComicVineGetAllIssuesAction</code> retrieves the metadata for all comics for a given
 * volume.
 *
 * @author Darryl L. Pierce
 */
@Log4j2
public class ComicVineGetAllIssuesAction
    extends AbstractComicVineScrapingAction<List<IssueDetailsMetadata>> {
  private ComicVineGetIssueWithDetailsAction getIssueWithDetailsAction =
      new ComicVineGetIssueWithDetailsAction();

  @Getter @Setter private String volumeId;

  @Override
  public List<IssueDetailsMetadata> execute() throws MetadataException {
    if (!StringUtils.hasLength(this.getApiKey())) throw new MetadataException("Missing API key");
    if (this.volumeId == null) throw new MetadataException("Missing volume id");

    this.addField("issues");
    this.addField("publisher");
    this.addField("name");
    this.addField("start_year");

    List<IssueDetailsMetadata> result = new ArrayList<>();
    boolean done = false;
    while (!done) {
      log.debug(
          "Creating url for: API key=****{} volume id={}", this.getMaskedApiKey(), this.volumeId);
      final String url =
          this.createUrl(this.baseUrl, String.format("volume/4050-%s", this.volumeId));
      final WebClient client = this.createWebClient(url);
      final Mono<ComicVineGetAllIssuesQueryResponse> request =
          client.get().uri(url).retrieve().bodyToMono(ComicVineGetAllIssuesQueryResponse.class);

      ComicVineGetAllIssuesQueryResponse response = null;

      try {
        response = request.block();
      } catch (Exception error) {
        throw new MetadataException("Failed to get response", error);
      }

      if (response == null) throw new MetadataException("No response received");

      log.debug(
          "Received: {} issue{}",
          response.getResults().getIssues().size(),
          response.getResults().getIssues().size() == 1 ? "" : "s");

      for (int index = 0; index < response.getResults().getIssues().size(); index++) {
        final ComicVineIssue issue = response.getResults().getIssues().get(index);

        final IssueDetailsMetadata entry = new IssueDetailsMetadata();
        entry.setSourceId(issue.getId());
        entry.setPublisher(response.getResults().getPublisher().getName().trim());
        entry.setSeries(response.getResults().getName().trim());
        entry.setIssueNumber(issue.getIssueNumber().trim());
        entry.setVolume(response.getResults().getStartYear().trim());
        entry.setTitle(issue.getTitle());
        entry.setCoverDate(issue.getCoverDate());
        entry.setStoreDate(issue.getStoreDate());

        try {
          log.trace("Fetching issue metadata: id={}", issue.getId());
          log.trace("Creating issue details action");
          this.getIssueWithDetailsAction.setBaseUrl(this.getBaseUrl());
          this.getIssueWithDetailsAction.setApiKey(this.getApiKey());
          this.getIssueWithDetailsAction.setIssueId(issue.getId());
        } catch (Exception error) {
          log.error("Failed to get issue cover and store dates", error);
        }

        result.add(entry);
      }

      done =
          response.getOffset() + response.getNumberOfPageResults()
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
}
