package com.breuninger.boot.mongo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static com.breuninger.boot.status.domain.Status.ERROR;
import static com.breuninger.boot.status.domain.Status.OK;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.mongodb.MongoException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoDatabase;

public class MongoStatusDetailIndicatorTest {

  @Mock
  private MongoDatabase mongoDatabase;

  private MongoStatusDetailIndicator testee;

  @Before
  public void setup() {
    initMocks(this);
    testee = new MongoStatusDetailIndicator(mongoDatabase);
  }

  @Test
  public void shouldReturnOKStatus() {
    // given
    when(mongoDatabase.runCommand(new Document().append("ping", 1))).thenReturn(new Document().append("ok", 1.0d));
    // when
    final var statusDetail = testee.statusDetail();
    // then
    assertThat(statusDetail.getStatus(), is(OK));
  }

  @Test
  public void shouldReturnErrorStatusWhenDatabaseDoesntReturnOKForPing() {
    // given
    when(mongoDatabase.runCommand(new Document().append("ping", 1))).thenReturn(new Document().append("error", 1.0d));
    // when
    final var statusDetail = testee.statusDetail();
    // then
    assertThat(statusDetail.getStatus(), is(ERROR));
    assertThat(statusDetail.getMessage(), containsString("Mongo database unreachable or ping command failed."));
  }

  @Test
  public void shouldReturnErrorStatusWhenDatabaseTimesOut() {
    // given
    when(mongoDatabase.runCommand(new Document().append("ping", 1))).thenThrow(new MongoTimeoutException("Timeout"));
    // when
    final var statusDetail = testee.statusDetail();
    // then
    assertThat(statusDetail.getStatus(), is(ERROR));
    assertThat(statusDetail.getMessage(), containsString("Mongo database check ran into timeout"));
  }

  @Test
  public void shouldReturnErrorStatusOnAnyException() {
    // given
    when(mongoDatabase.runCommand(new Document().append("ping", 1))).thenThrow(new MongoException("SomeException"));
    // when
    final var statusDetail = testee.statusDetail();
    // then
    assertThat(statusDetail.getStatus(), is(ERROR));
    assertThat(statusDetail.getMessage(), containsString("Exception during database check"));
  }
}
