/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2020, The ComiXed Project
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

package org.comixedproject.metadata.comicvine.adaptors;

import static org.comixedproject.metadata.comicvine.adaptors.ComicVineMetadataAdaptorProvider.*;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.metadata.MetadataException;
import org.comixedproject.metadata.adaptors.AbstractMetadataAdaptor;
import org.comixedproject.metadata.adaptors.MetadataAdaptor;
import org.comixedproject.metadata.comicvine.actions.*;
import org.comixedproject.metadata.model.*;
import org.comixedproject.model.metadata.MetadataSource;

/**
 * <code>ComicVineMetadataAdaptor</code> provides an implementation of {@link MetadataAdaptor} for
 * ComicVine.
 *
 * @author Darryl L. Pierce
 */
@Log4j2
public class ComicVineMetadataAdaptor extends AbstractMetadataAdaptor {
  static final String REFERENCE_ID_PATTERN =
      "^https?\\:\\/\\/(www\\.comicvine\\.com|comicvine\\.gamespot\\.com)\\/.*\\/4000-([\\d]+).*";
  /** The base URL for ComicVine. */
  public static final String BASE_URL = "https://comicvine.gamespot.com";

  public static final long MINIMUM_DELAY_VALUE = 1L;
  public static final int REFERENCE_ID_POSITION = 2;

  public ComicVineMetadataAdaptor() {
    super("ComiXed ComicVine Scraper", PROVIDER_NAME);
  }

  @Override
  public List<StoryMetadata> getStories(
      final String storyName, final Integer maxRecords, final MetadataSource metadataSource)
      throws MetadataException {
    log.debug("Fetching stories from ComicVine: storyName={}", storyName);
    final ComicVineGetStoriesAction action = new ComicVineGetStoriesAction();
    action.setBaseUrl(BASE_URL);
    action.setApiKey(
        this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_API_KEY, true));
    action.setDelay(this.doGetDelayValue(metadataSource));
    action.setStoryName(storyName);
    action.setMaxRecords(maxRecords);

    log.debug("Executing action");
    final List<StoryMetadata> result = action.execute();

    log.debug("Returning {} stories", result.size());
    return result;
  }

  @Override
  public StoryDetailMetadata getStory(final String referenceId, final MetadataSource metadataSource)
      throws MetadataException {
    log.debug("Fetching story details: referenceId={}", referenceId);
    final ComicVineGetStoryDetailAction action = new ComicVineGetStoryDetailAction();
    action.setBaseUrl(BASE_URL);
    action.setApiKey(
        this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_API_KEY, true));
    action.setDelay(this.doGetDelayValue(metadataSource));
    action.setReferenceId(referenceId);

    log.debug("Executing action");
    final StoryDetailMetadata result = action.execute();

    log.debug("Returning one story with {} issues", result.getIssues().size());
    return result;
  }

  @Override
  public List<VolumeMetadata> getVolumes(
      final String seriesName, final Integer maxRecords, final MetadataSource metadataSource)
      throws MetadataException {
    return this.doGetVolumes(
        seriesName, maxRecords, metadataSource, new ComicVineGetVolumesAction());
  }

  List<VolumeMetadata> doGetVolumes(
      final String seriesName,
      final Integer maxRecords,
      final MetadataSource metadataSource,
      final ComicVineGetVolumesAction action)
      throws MetadataException {
    log.debug("Fetching volumes from ComicVine: seriesName={}", seriesName);

    action.setBaseUrl(BASE_URL);
    action.setApiKey(
        this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_API_KEY, true));
    action.setDelay(this.doGetDelayValue(metadataSource));
    action.setSeries(seriesName);
    action.setMaxRecords(maxRecords);

    log.debug("Executing action");
    final List<VolumeMetadata> result = action.execute();

    log.debug("Returning {} volume{}", result.size(), result.size() == 1 ? "" : "s");
    return result;
  }

  @Override
  public List<IssueDetailsMetadata> getAllIssues(
      final String volume, final MetadataSource metadataSource) throws MetadataException {
    return this.doGetAllIssues(volume, metadataSource, new ComicVineGetAllIssuesAction());
  }

  List<IssueDetailsMetadata> doGetAllIssues(
      final String volume,
      final MetadataSource metadataSource,
      final ComicVineGetAllIssuesAction action)
      throws MetadataException {
    log.debug("Fetching the list of all issues from ComicVine: volume={}", volume);

    action.setBaseUrl(BASE_URL);
    action.setApiKey(
        this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_API_KEY, true));
    action.setDelay(this.doGetDelayValue(metadataSource));
    action.setVolumeId(volume);

    log.debug("Executing action");
    final List<IssueDetailsMetadata> result = action.execute();

    log.debug("Returning {} issue{}", result.size(), result.size() == 1 ? "" : "s");
    return result;
  }

  @Override
  public IssueMetadata doGetIssue(
      final String volume, final String issueNumber, final MetadataSource metadataSource)
      throws MetadataException {
    return this.doGetIssue(volume, issueNumber, metadataSource, new ComicVineGetIssueAction());
  }

  IssueMetadata doGetIssue(
      final String volume,
      final String issueNumber,
      final MetadataSource metadataSource,
      final ComicVineGetIssueAction action)
      throws MetadataException {
    log.debug("Fetching issue from ComicVine: volume={} issueNumber={}", volume, issueNumber);

    action.setBaseUrl(BASE_URL);
    action.setApiKey(
        this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_API_KEY, true));
    action.setVolumeId(volume);
    action.setIssueNumber(issueNumber);

    final List<IssueMetadata> result = action.execute();

    return result.isEmpty() ? null : result.get(0);
  }

  @Override
  public IssueDetailsMetadata getIssueDetails(
      final String issueId, final MetadataSource metadataSource) throws MetadataException {
    return this.doGetIssueDetails(issueId, metadataSource, new ComicVineGetIssueDetailsAction());
  }

  IssueDetailsMetadata doGetIssueDetails(
      final String issueId,
      final MetadataSource metadataSource,
      final ComicVineGetIssueDetailsAction action)
      throws MetadataException {
    log.debug("Fetching issue details: issueId={}", issueId);

    action.setBaseUrl(BASE_URL);
    action.setApiKey(
        this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_API_KEY, true));
    action.setIssueId(issueId);

    return action.execute();
  }

  @Override
  public String getReferenceId(final String webAddress) {
    final Pattern pattern = Pattern.compile(REFERENCE_ID_PATTERN);
    final Matcher matches = pattern.matcher(webAddress);
    String referenceId = null;
    if (matches.matches()) {
      referenceId = matches.group(REFERENCE_ID_POSITION);
    }
    return referenceId;
  }

  private long doGetDelayValue(final MetadataSource metadataSource) {
    long result = MINIMUM_DELAY_VALUE;
    try {
      final String defined =
          this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_DELAY, false);
      if (!Objects.isNull(defined)) {
        result = Long.parseLong(defined);
      }
    } catch (MetadataException | NumberFormatException error) {
      log.error("Failed to load property: " + PROPERTY_DELAY, error);
    }
    if (result < MINIMUM_DELAY_VALUE) {
      result = MINIMUM_DELAY_VALUE;
    }
    log.trace("Returning delay value: {}", result);
    return result;
  }
}
