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

import static junit.framework.TestCase.assertNotNull;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.comixedproject.metadata.MetadataException;
import org.comixedproject.metadata.model.IssueDetailsMetadata;
import org.comixedproject.metadata.model.StoryDetailMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RunWith(MockitoJUnitRunner.class)
public class ComicVineGetStoryDetailActionTest {
  private static final String TEST_API_KEY = "This.Is.A.Test.Key";
  private static final String TEST_STORY_RESPONSE =
      "{\"error\":\"OK\",\"limit\":1,\"offset\":0,\"number_of_page_results\":1,\"number_of_total_results\":1,\"status_code\":1,\"results\":{\"description\":null,\"id\":61035,\"issues\":[{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-935767\\/\",\"id\":935767,\"name\":\"The Red Fist Saga Part 1; The Island\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-1-the-red-fist-saga-part-1-the-island\\/4000-935767\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-942720\\/\",\"id\":942720,\"name\":\"The Red Fist Saga Part 2; The Hand\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-2-the-red-fist-saga-part-2-the-hand\\/4000-942720\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-946713\\/\",\"id\":946713,\"name\":\"The Red Fist Saga Part 3\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-3-the-red-fist-saga-part-3\\/4000-946713\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-950374\\/\",\"id\":950374,\"name\":\"The Red Fist Saga Part 4\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-4-the-red-fist-saga-part-4\\/4000-950374\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-956788\\/\",\"id\":956788,\"name\":\"The Red Fist Saga Part 5\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-5-the-red-fist-saga-part-5\\/4000-956788\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-980931\\/\",\"id\":980931,\"name\":\"Vol. 1: The Red Fist Saga Part One\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-and-elektra-by-chip-zdarsky-1-vol-1-the-\\/4000-980931\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-958981\\/\",\"id\":958981,\"name\":\"The Red Fist Saga Part 6\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-6-the-red-fist-saga-part-6\\/4000-958981\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-963989\\/\",\"id\":963989,\"name\":\"The Red Fist Saga Part 7\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-7-the-red-fist-saga-part-7\\/4000-963989\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-969380\\/\",\"id\":969380,\"name\":\"The Red Fist Saga Part 8\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-8-the-red-fist-saga-part-8\\/4000-969380\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-979431\\/\",\"id\":979431,\"name\":\"The Red Fist Saga Part 9\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-9-the-red-fist-saga-part-9\\/4000-979431\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-985648\\/\",\"id\":985648,\"name\":\"The Red Fist Saga Part 10\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-10-the-red-fist-saga-part-10\\/4000-985648\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-988121\\/\",\"id\":988121,\"name\":\"The Red Fist Saga Part 11; Painful Lesson\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-11-the-red-fist-saga-part-11-painful-les\\/4000-988121\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-1003838\\/\",\"id\":1003838,\"name\":\"Vol. 2: The Red Fist Saga Part Two\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-and-elektra-by-chip-zdarsky-2-vol-2-the-\\/4000-1003838\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-993228\\/\",\"id\":993228,\"name\":\"The Red Fist Saga Part 12\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-12-the-red-fist-saga-part-12\\/4000-993228\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-998583\\/\",\"id\":998583,\"name\":\"The Red Fist Saga, Part 13\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-13-the-red-fist-saga-part-13\\/4000-998583\\/\"},{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/issue\\/4000-1008950\\/\",\"id\":1008950,\"name\":\"The Red Fist Saga, Conclusion \",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/daredevil-14-the-red-fist-saga-conclusion\\/4000-1008950\\/\"}],\"name\":\"\\\"Daredevil\\\" The Red Fist Saga\",\"publisher\":null},\"version\":\"1.0\"}";
  private static final String TEST_REFERENCE_ID = "337";

  @InjectMocks private ComicVineGetStoryDetailAction action;
  @Mock private ComicVineGetIssueDetailsAction getIssueDetailsAction;
  @Mock private IssueDetailsMetadata storyIssueMetadata;

  private MockWebServer comicVineServer;

  @Before
  public void setUp() throws IOException {
    action.getIssueDetailsAction = getIssueDetailsAction;

    comicVineServer = new MockWebServer();
    comicVineServer.start();

    final String hostname = String.format("http://localhost:%s", this.comicVineServer.getPort());
    action.setBaseUrl(hostname);
    action.setApiKey(TEST_API_KEY);
    action.setReferenceId(TEST_REFERENCE_ID);
  }

  @Test(expected = MetadataException.class)
  public void testExecuteFailsWithoutApiKey() throws MetadataException {
    action.setApiKey("");
    action.execute();
  }

  @Test(expected = MetadataException.class)
  public void testExecuteFailsWithoutReferenceId() throws MetadataException {
    action.setReferenceId(null);
    action.execute();
  }

  @Test
  public void testExecute() throws MetadataException {
    this.comicVineServer.enqueue(
        new MockResponse()
            .setBody(TEST_STORY_RESPONSE)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
    Mockito.when(getIssueDetailsAction.execute()).thenReturn(storyIssueMetadata);

    final StoryDetailMetadata result = action.execute();

    assertNotNull(result);
  }
}
