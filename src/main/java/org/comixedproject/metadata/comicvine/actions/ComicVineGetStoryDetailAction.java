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

import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.metadata.MetadataException;
import org.comixedproject.metadata.comicvine.model.ComicVineGetStoryDetailResponse;
import org.comixedproject.metadata.comicvine.model.ComicVineIssue;
import org.comixedproject.metadata.model.IssueDetailsMetadata;
import org.comixedproject.metadata.model.StoryDetailMetadata;
import org.comixedproject.metadata.model.StoryIssueMetadata;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * <code>ComicVineGetStoryDetailAction</code> fetches the metadata for a single story arc from
 * ComicVine.
 *
 * @author Darryl L. Pierce
 */
@NoArgsConstructor
@Log4j2
public class ComicVineGetStoryDetailAction
    extends AbstractComicVineScrapingAction<StoryDetailMetadata> {
  @Getter @Setter private String referenceId;

  ComicVineGetIssueDetailsAction getIssueDetailsAction = new ComicVineGetIssueDetailsAction();

  @Override
  public StoryDetailMetadata execute() throws MetadataException {
    if (!StringUtils.hasLength(this.getApiKey())) throw new MetadataException("Missing API key");
    if (this.referenceId == null) throw new MetadataException("Missing reference id");

    this.addField("id");
    this.addField("publisher");
    this.addField("name");
    this.addField("description");
    this.addField("issues");

    final StoryDetailMetadata result = new StoryDetailMetadata();
    log.debug(
        "Creating url for: API key=****{} reference id={}",
        this.getMaskedApiKey(),
        this.referenceId);
    final String url =
        this.createUrl(this.baseUrl, String.format("story_arc/4045-%s", this.referenceId));
    final WebClient client = this.createWebClient(url);

    final Mono<ComicVineGetStoryDetailResponse> request =
        client.get().uri(url).retrieve().bodyToMono(ComicVineGetStoryDetailResponse.class);

    ComicVineGetStoryDetailResponse response = null;

    try {
      response = request.block();
    } catch (Exception error) {
      throw new MetadataException("Failed to get response", error);
    }

    if (response == null) throw new MetadataException("No response received");

    log.debug("Received response with {} issue(s)", response.getResults().getIssues().size());

    result.setReferenceId(this.referenceId);
    if (Objects.nonNull(response.getResults().getPublisher())) {
      result.setPublisher(response.getResults().getPublisher().getName().trim());
    }
    result.setName(response.getResults().getName());
    result.setDescription(response.getResults().getDescription());
    this.getIssueDetailsAction.setBaseUrl(this.baseUrl);
    this.getIssueDetailsAction.setApiKey(this.getApiKey());

    for (int index = 0; index < response.getResults().getIssues().size(); index++) {
      final ComicVineIssue entry = response.getResults().getIssues().get(index);
      this.getIssueDetailsAction.setIssueId(entry.getId());

      final IssueDetailsMetadata issue = this.getIssueDetailsAction.execute();
      final StoryIssueMetadata issueMetadata = new StoryIssueMetadata();
      issueMetadata.setReadingOrder(index + 1);
      issueMetadata.setName(issue.getSeries());
      issueMetadata.setVolume(issue.getVolume());
      issueMetadata.setIssueNumber(issue.getIssueNumber());
      issueMetadata.setCoverDate(issue.getCoverDate());
      result.getIssues().add(issueMetadata);
    }

    return result;
  }
}
