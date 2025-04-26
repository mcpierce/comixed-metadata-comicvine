package org.comixedproject.metadata.comicvine.actions;

import static junit.framework.TestCase.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.io.FileUtils;
import org.comixedproject.metadata.MetadataException;
import org.comixedproject.metadata.model.StoryMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RunWith(MockitoJUnitRunner.class)
public class ComicVineGetStoriesActionTest {
  private static final String TEST_API_KEY = "OICU812";
  private static final long TEST_DELAY = 1L;
  private static final String TEST_STORY_NAME = "\"The Mighty Avengers\" The Ultron Initiative";
  private static final String TEST_REFERENCE_ID = "54894";
  private static final String TEST_BAD_RESPONSE_BODY = "This is not JSON";
  private static final String TEST_RESPONSE_BODY_FILE =
      "src/test/resources/story-list-metadata.json";
  private static final String TEST_IMAGE_URL =
      "https://comicvine.gamespot.com/a/uploads/original/1/10390/390031-69520-the-ultron-initiativ.JPG";
  private static final String TEST_PUBLISHER_NAME = "Marvel";
  private static final Integer TEST_ALL_RECORDS = 7;
  private static final Integer TEST_MAX_RECORDS = 3;
  public MockWebServer comicVineServer;

  @InjectMocks private ComicVineGetStoriesAction action;

  private String responseBody;

  @Before
  public void setUp() throws IOException {
    responseBody = FileUtils.readFileToString(new File(TEST_RESPONSE_BODY_FILE), "UTF-8");
    comicVineServer = new MockWebServer();
    comicVineServer.start();

    final String hostname = String.format("http://localhost:%s", this.comicVineServer.getPort());
    action.setBaseUrl(hostname);
    action.setApiKey(TEST_API_KEY);
    action.setDelay(TEST_DELAY);
    action.setStoryName(TEST_STORY_NAME);
  }

  @After
  public void tearDown() throws IOException {
    comicVineServer.shutdown();
  }

  @Test(expected = MetadataException.class)
  public void testExecuteMissingApiKey() throws MetadataException {
    action.setApiKey("");
    action.execute();
  }

  @Test(expected = MetadataException.class)
  public void testExecuteMissingStoryName() throws MetadataException {
    action.setStoryName("");

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
            .setBody(this.responseBody)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

    action.setMaxRecords(0);

    final List<StoryMetadata> result = action.execute();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(TEST_ALL_RECORDS.intValue(), result.size());

    final StoryMetadata story = result.get(0);

    assertEquals(TEST_STORY_NAME, story.getName());
    assertEquals(TEST_PUBLISHER_NAME, story.getPublisher());
    assertEquals(TEST_REFERENCE_ID, story.getReferenceId());
    assertEquals(TEST_IMAGE_URL, story.getImageUrl());
  }

  @Test
  public void testExecuteWithLimit() throws MetadataException {
    this.comicVineServer.enqueue(
        new MockResponse()
            .setBody(this.responseBody)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

    action.setMaxRecords(TEST_MAX_RECORDS);

    final List<StoryMetadata> result = action.execute();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(TEST_MAX_RECORDS.intValue(), result.size());

    final StoryMetadata story = result.get(0);

    assertEquals(TEST_STORY_NAME, story.getName());
    assertEquals(TEST_PUBLISHER_NAME, story.getPublisher());
    assertEquals(TEST_REFERENCE_ID, story.getReferenceId());
    assertEquals(TEST_IMAGE_URL, story.getImageUrl());
  }
}
