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

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;

import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.comixedproject.metadata.MetadataException;
import org.comixedproject.metadata.model.IssueMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RunWith(MockitoJUnitRunner.class)
public class ComicVineGetIssueActionTest {
  private static final String TEST_API_KEY = "This.is.the.test.api.key";
  private static final String TEST_VOLUME_ID = "219";
  private static final String TEST_ISSUE_NUMBER = "989";
  private static final String TEST_BAD_RESPONSE_BODY = "this is not JSON";
  private static final String TEST_GOOD_BODY =
      "{\"error\":\"OK\",\"limit\":100,\"offset\":0,\"number_of_page_results\":1,\"number_of_total_results\":1,\"status_code\":1,\"results\":[{\"cover_date\":\"2012-05-01\",\"description\":null,\"id\":421092,\"image\":{\"icon_url\":\"https:\\/\\/comicvine1.cbsistatic.com\\/uploads\\/square_avatar\\/12\\/124613\\/3227301-action%20lab%20confidential%20v1%20%282012%29%20pagecover.jpg\",\"medium_url\":\"https:\\/\\/comicvine1.cbsistatic.com\\/uploads\\/scale_medium\\/12\\/124613\\/3227301-action%20lab%20confidential%20v1%20%282012%29%20pagecover.jpg\",\"screen_url\":\"https:\\/\\/comicvine1.cbsistatic.com\\/uploads\\/screen_medium\\/12\\/124613\\/3227301-action%20lab%20confidential%20v1%20%282012%29%20pagecover.jpg\",\"screen_large_url\":\"https:\\/\\/comicvine1.cbsistatic.com\\/uploads\\/screen_kubrick\\/12\\/124613\\/3227301-action%20lab%20confidential%20v1%20%282012%29%20pagecover.jpg\",\"small_url\":\"https:\\/\\/comicvine1.cbsistatic.com\\/uploads\\/scale_small\\/12\\/124613\\/3227301-action%20lab%20confidential%20v1%20%282012%29%20pagecover.jpg\",\"super_url\":\"https:\\/\\/comicvine1.cbsistatic.com\\/uploads\\/scale_large\\/12\\/124613\\/3227301-action%20lab%20confidential%20v1%20%282012%29%20pagecover.jpg\",\"thumb_url\":\"https:\\/\\/comicvine1.cbsistatic.com\\/uploads\\/scale_avatar\\/12\\/124613\\/3227301-action%20lab%20confidential%20v1%20%282012%29%20pagecover.jpg\",\"tiny_url\":\"https:\\/\\/comicvine1.cbsistatic.com\\/uploads\\/square_mini\\/12\\/124613\\/3227301-action%20lab%20confidential%20v1%20%282012%29%20pagecover.jpg\",\"original_url\":\"https:\\/\\/comicvine1.cbsistatic.com\\/uploads\\/original\\/12\\/124613\\/3227301-action%20lab%20confidential%20v1%20%282012%29%20pagecover.jpg\",\"image_tags\":\"All Images\"},\"issue_number\":\"1\",\"store_date\":\"2012-05-05\",\"volume\":{\"api_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/api\\/volume\\/4050-66143\\/\",\"id\":66143,\"name\":\"Action Lab Confidential\",\"site_detail_url\":\"https:\\/\\/comicvine.gamespot.com\\/action-lab-confidential\\/4050-66143\\/\"}}],\"version\":\"1.0\"}";

  @InjectMocks private ComicVineGetIssueAction action;
  private MockWebServer comicVineServer;

  @Before
  public void setUp() throws IOException {
    comicVineServer = new MockWebServer();
    comicVineServer.start();

    final String hostname = String.format("http://localhost:%s", this.comicVineServer.getPort());
    action.setBaseUrl(hostname);
    action.setApiKey(TEST_API_KEY);
    action.setVolumeId(TEST_VOLUME_ID);
    action.setIssueNumber(TEST_ISSUE_NUMBER);
  }

  @After
  public void tearDown() throws IOException {
    comicVineServer.shutdown();
  }

  @Test(expected = MetadataException.class)
  public void testExecuteWithoutApiKey() throws MetadataException {
    action.setApiKey("");
    action.execute();
  }

  @Test(expected = MetadataException.class)
  public void testExecuteWithoutVolumeId() throws MetadataException {
    action.setVolumeId(null);
    action.execute();
  }

  @Test(expected = MetadataException.class)
  public void testExecuteBadResponse() throws MetadataException {
    this.comicVineServer.enqueue(
        new MockResponse()
            .setBody(TEST_BAD_RESPONSE_BODY)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

    action.execute();
  }

  @Test
  public void testExecute() throws MetadataException {
    this.comicVineServer.enqueue(
        new MockResponse()
            .setBody(TEST_GOOD_BODY)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

    final List<IssueMetadata> result = action.execute();

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }
}
