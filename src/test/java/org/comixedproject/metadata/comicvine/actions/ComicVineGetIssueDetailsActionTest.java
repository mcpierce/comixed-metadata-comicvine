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

package org.comixedproject.metadata.comicvine.actions;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.comixedproject.metadata.MetadataException;
import org.comixedproject.metadata.comicvine.adaptors.ComicVineMetadataAdaptor;
import org.comixedproject.metadata.comicvine.model.*;
import org.comixedproject.metadata.model.IssueDetailsMetadata;
import org.comixedproject.model.comicbooks.ComicTagType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ComicVineGetIssueDetailsActionTest {
  private static final String TEST_API_KEY = "This.Is.A.Test.Key";
  private static final String TEST_ISSUE_ID = "337";
  private static final String TEST_VOLUME_NAME = "Volume Name";
  private static final String TEST_PUBLISHER_NAME = "Publisher Name";
  private static final String TEST_VOLUME_DETAILS_URL = "http://comicvine.gamespot.com/volume.url";
  private static final String TEST_PUBLISHER_DETAILS_API =
      "http://comicvine.gamespot.com/publisher_url";
  private static final String TEST_DESCRIPTION = "The issue description";
  private static final String TEST_START_YEAR = "2020";
  private static final String TEST_ISSUE_NUMBER = "23";
  private static final Date TEST_COVER_DATE = new Date();
  private static final String TEST_CHARACTER_NAME = "Character Name";
  private static final String TEST_TEAM_NAME = "Team Name";
  private static final String TEST_LOCATION_NAME = "Location Name";
  private static final String TEST_STORY_NAME = "Story Name";
  private static final String TEST_CREDIT_NAME = "Credit Name";
  private static final String TEST_CREDIT_ROLE_1 = ComicVineCreditType.PENCILLER.getTagValue();
  private static final String TEST_CREDIT_TAG_1 = ComicTagType.PENCILLER.getValue();
  private static final String TEST_CREDIT_ROLE_2 = ComicVineCreditType.EDITOR.getTagValue();
  private static final String TEST_CREDIT_TAG_2 = ComicTagType.EDITOR.getValue();
  private static final String TEST_COMIC_VINE_ISSUE_ID = "71765";

  @InjectMocks private ComicVineGetIssueDetailsAction action;
  @Mock private ComicVineGetIssueWithDetailsAction getIssueWithDetailsAction;
  @Mock private ComicVineGetVolumeDetailsAction getVolumeDetailsAction;
  @Mock private ComicVineGetPublisherDetailsAction getPublisherDetailsAction;
  @Mock private IssueDetailsMetadata issueDetailsMetadata;
  @Mock private ComicVineIssue comicVineIssue;
  @Mock private ComicVineVolume comicVineVolume;
  @Mock private ComicVinePublisher comicVinePublisher;
  @Mock private ComicVineCharacter comicVineCharacter;
  @Mock private ComicVineTeam comicVineTeam;
  @Mock private ComicVineLocation comicVineLocation;
  @Mock private ComicVineStory comicVineStory;
  @Mock private ComicVineCredit comicVineCredit;

  private List<ComicVineCharacter> characters = new ArrayList<>();
  private List<ComicVineTeam> teams = new ArrayList<>();
  private List<ComicVineLocation> locations = new ArrayList<>();
  private List<ComicVineStory> stories = new ArrayList<>();
  private List<ComicVineCredit> credits = new ArrayList<>();

  @Before
  public void setUp() {
    action.getIssueWithDetailsAction = this.getIssueWithDetailsAction;
    action.getVolumeDetailsAction = this.getVolumeDetailsAction;
    action.getPublisherDetailsAction = this.getPublisherDetailsAction;

    action.setBaseUrl(ComicVineMetadataAdaptor.BASE_URL);
    action.setApiKey(TEST_API_KEY);
    action.setIssueId(TEST_ISSUE_ID);

    characters.add(comicVineCharacter);
    teams.add(comicVineTeam);
    locations.add(comicVineLocation);
    stories.add(comicVineStory);
    credits.add(comicVineCredit);

    Mockito.when(comicVineIssue.getVolume()).thenReturn(comicVineVolume);
    Mockito.when(comicVineIssue.getCoverDate()).thenReturn(TEST_COVER_DATE);
    Mockito.when(comicVineIssue.getIssueNumber()).thenReturn(TEST_ISSUE_NUMBER);
    Mockito.when(comicVineIssue.getDescription()).thenReturn(TEST_DESCRIPTION);

    Mockito.when(comicVineVolume.getPublisher()).thenReturn(comicVinePublisher);
    Mockito.when(comicVineVolume.getDetailUrl()).thenReturn(TEST_VOLUME_DETAILS_URL);
    Mockito.when(comicVineVolume.getName()).thenReturn(TEST_VOLUME_NAME);
    Mockito.when(comicVineVolume.getStartYear()).thenReturn(TEST_START_YEAR);

    Mockito.when(comicVinePublisher.getName()).thenReturn(TEST_PUBLISHER_NAME);
    Mockito.when(comicVinePublisher.getDetailUrl()).thenReturn(TEST_PUBLISHER_DETAILS_API);

    Mockito.when(comicVineIssue.getId()).thenReturn(TEST_COMIC_VINE_ISSUE_ID);
    Mockito.when(comicVineIssue.getCharacters()).thenReturn(characters);
    Mockito.when(comicVineCharacter.getName()).thenReturn(TEST_CHARACTER_NAME);
    Mockito.when(comicVineIssue.getTeams()).thenReturn(teams);
    Mockito.when(comicVineTeam.getName()).thenReturn(TEST_TEAM_NAME);
    Mockito.when(comicVineIssue.getLocations()).thenReturn(locations);
    Mockito.when(comicVineLocation.getName()).thenReturn(TEST_LOCATION_NAME);
    Mockito.when(comicVineIssue.getStories()).thenReturn(stories);
    Mockito.when(comicVineStory.getName()).thenReturn(TEST_STORY_NAME);
    Mockito.when(comicVineIssue.getPeople()).thenReturn(credits);
    Mockito.when(comicVineCredit.getName()).thenReturn(TEST_CREDIT_NAME);
    Mockito.when(comicVineCredit.getRole())
        .thenReturn(String.format("%s, %s", TEST_CREDIT_ROLE_1, TEST_CREDIT_ROLE_2));
  }

  @Test(expected = MetadataException.class)
  public void testExecuteFailsWithoutApiKey() throws MetadataException {
    action.setApiKey("");
    action.execute();
  }

  @Test(expected = MetadataException.class)
  public void testExecuteFailsWithoutIssueId() throws MetadataException {
    action.setIssueId(null);
    action.execute();
  }

  @Test(expected = MetadataException.class)
  public void testExecuteGetIssueDetailsThrowsException() throws MetadataException {
    Mockito.when(getIssueWithDetailsAction.execute()).thenThrow(MetadataException.class);

    try {
      action.execute();
    } finally {
      this.verifyGetIssueDetailsAction();
    }
  }

  @Test(expected = MetadataException.class)
  public void testExecuteGetVolumeDetailsThrowsException() throws MetadataException {
    Mockito.when(getIssueWithDetailsAction.execute()).thenReturn(comicVineIssue);
    Mockito.when(getVolumeDetailsAction.execute()).thenThrow(MetadataException.class);

    try {
      action.execute();
    } finally {
      this.verifyGetIssueDetailsAction();
      this.verifyGetVolumeDetailsAction();
    }
  }

  @Test(expected = MetadataException.class)
  public void testExecuteGetPublisherDetailsThrowsException() throws MetadataException {
    Mockito.when(getIssueWithDetailsAction.execute()).thenReturn(comicVineIssue);
    Mockito.when(getVolumeDetailsAction.execute()).thenReturn(comicVineVolume);
    Mockito.when(getPublisherDetailsAction.execute()).thenThrow(MetadataException.class);

    try {
      action.execute();
    } finally {
      this.verifyGetIssueDetailsAction();
      this.verifyGetVolumeDetailsAction();
      this.verifyGetPublisherDetailsAction();
    }
  }

  @Test
  public void testExecute() throws MetadataException {
    Mockito.when(getIssueWithDetailsAction.execute()).thenReturn(comicVineIssue);
    Mockito.when(getVolumeDetailsAction.execute()).thenReturn(comicVineVolume);
    Mockito.when(getPublisherDetailsAction.execute()).thenReturn(comicVinePublisher);

    final IssueDetailsMetadata result = action.execute();

    this.verifyGetIssueDetailsAction();
    this.verifyGetVolumeDetailsAction();
    this.verifyGetPublisherDetailsAction();

    assertEquals(TEST_COMIC_VINE_ISSUE_ID, result.getSourceId());
    assertEquals(TEST_PUBLISHER_NAME, result.getPublisher());
    assertEquals(TEST_VOLUME_NAME, result.getSeries());
    assertEquals(TEST_START_YEAR, result.getVolume());
    assertEquals(TEST_ISSUE_NUMBER, result.getIssueNumber());
    assertEquals(TEST_COVER_DATE, result.getCoverDate());
    assertEquals(TEST_DESCRIPTION, result.getDescription());

    assertFalse(result.getCharacters().isEmpty());
    for (String character : result.getCharacters()) {
      assertEquals(TEST_CHARACTER_NAME, character);
    }
    assertFalse(result.getTeams().isEmpty());
    for (String team : result.getTeams()) {
      assertEquals(TEST_TEAM_NAME, team);
    }
    assertFalse(result.getLocations().isEmpty());
    for (String location : result.getLocations()) {
      assertEquals(TEST_LOCATION_NAME, location);
    }
    assertFalse(result.getStories().isEmpty());
    for (String story : result.getStories()) {
      assertEquals(TEST_STORY_NAME, story);
    }
    assertFalse(result.getCredits().isEmpty());
    assertEquals(TEST_CREDIT_NAME, result.getCredits().get(0).getName());
    assertEquals(TEST_CREDIT_TAG_1, result.getCredits().get(0).getRole());
    assertEquals(TEST_CREDIT_NAME, result.getCredits().get(1).getName());
    assertEquals(TEST_CREDIT_TAG_2, result.getCredits().get(1).getRole());
  }

  private void verifyGetPublisherDetailsAction() throws MetadataException {
    Mockito.verify(getPublisherDetailsAction, Mockito.times(1))
        .setApiUrl(TEST_PUBLISHER_DETAILS_API);
    Mockito.verify(getPublisherDetailsAction, Mockito.times(1)).setApiKey(TEST_API_KEY);
    Mockito.verify(getPublisherDetailsAction, Mockito.times(1)).execute();
  }

  private void verifyGetVolumeDetailsAction() throws MetadataException {
    Mockito.verify(getVolumeDetailsAction, Mockito.times(1)).setApiUrl(TEST_VOLUME_DETAILS_URL);
    Mockito.verify(getVolumeDetailsAction, Mockito.times(1)).setApiKey(TEST_API_KEY);
    Mockito.verify(getVolumeDetailsAction, Mockito.times(1)).execute();
  }

  private void verifyGetIssueDetailsAction() throws MetadataException {
    Mockito.verify(getIssueWithDetailsAction, Mockito.times(1))
        .setBaseUrl(ComicVineMetadataAdaptor.BASE_URL);
    Mockito.verify(getIssueWithDetailsAction, Mockito.times(1)).setApiKey(TEST_API_KEY);
    Mockito.verify(getIssueWithDetailsAction, Mockito.times(1)).setIssueId(TEST_ISSUE_ID);
    Mockito.verify(getIssueWithDetailsAction, Mockito.times(1)).execute();
  }
}
