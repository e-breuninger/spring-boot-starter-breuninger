package com.breuninger.boot.mongo;

import static com.breuninger.boot.status.domain.Status.ERROR;
import static com.breuninger.boot.status.domain.Status.OK;

import org.bson.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.breuninger.boot.status.domain.StatusDetail;
import com.breuninger.boot.status.indicator.StatusDetailIndicator;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoDatabase;

@Component
@ConditionalOnProperty(prefix = "breuninger.mongo.status", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MongoStatusDetailIndicator implements StatusDetailIndicator {

  private final MongoDatabase mongoDatabase;

  public MongoStatusDetailIndicator(final MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
  }

  @Override
  public StatusDetail statusDetail() {
    final var databaseStatusName = "MongoDB Status";
    final var document = new Document().append("ping", 1);
    final Document answer;
    try {
      answer = mongoDatabase.runCommand(document);
    } catch (final MongoTimeoutException e) {
      return StatusDetail.statusDetail(databaseStatusName, ERROR,
        "Mongo database check ran into timeout (" + e.getMessage() + ").");
    } catch (final Exception other) {
      return StatusDetail.statusDetail(databaseStatusName, ERROR,
        "Exception during database check (" + other.getMessage() + ").");
    }

    if (answer != null && answer.get("ok") != null && (Double) answer.get("ok") == 1.0d) {
      return StatusDetail.statusDetail(databaseStatusName, OK, "Mongo database is reachable.");
    }

    return StatusDetail.statusDetail(databaseStatusName, ERROR, "Mongo database unreachable or ping command failed.");
  }
}
