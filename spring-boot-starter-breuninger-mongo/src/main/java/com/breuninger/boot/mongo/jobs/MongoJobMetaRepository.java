package com.breuninger.boot.mongo.jobs;

import static java.util.Collections.emptyMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.StreamSupport.stream;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Updates.unset;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.breuninger.boot.jobs.domain.JobMeta;
import com.breuninger.boot.jobs.repository.JobMetaRepository;
import com.breuninger.boot.mongo.configuration.MongoProperties;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;

public class MongoJobMetaRepository implements JobMetaRepository {

  private static final FindOneAndUpdateOptions UPSERT = new FindOneAndUpdateOptions().upsert(true)
    .maxTime(250, TimeUnit.MILLISECONDS);
  private static final String ID = "_id";
  private static final String KEY_DISABLED = "_e_disabled";
  private static final String KEY_RUNNING = "_e_running";

  private final MongoCollection<Document> collection;
  private final MongoProperties mongoProperties;

  public MongoJobMetaRepository(final MongoDatabase mongoDatabase, final String jobMetaCollectionName,
                                final MongoProperties mongoProperties) {
    this.mongoProperties = mongoProperties;
    final var tmpCollection = mongoDatabase.getCollection(jobMetaCollectionName);
    collection = tmpCollection.withWriteConcern(
      tmpCollection.getWriteConcern().withWTimeout(mongoProperties.getDefaultWriteTimeout(), TimeUnit.MILLISECONDS));
  }

  @Override
  public JobMeta getJobMeta(final String jobType) {
    final var document = collection.find(eq(ID, jobType))
      .maxTime(mongoProperties.getDefaultReadTimeout(), TimeUnit.MILLISECONDS)
      .first();
    if (document != null) {
      final var meta = document.keySet()
        .stream()
        .filter(key -> !key.startsWith("_e_") && !key.equals(ID))
        .collect(toMap(identity(), document::getString));
      final var isRunning = document.containsKey(KEY_RUNNING);
      final var isDisabled = document.containsKey(KEY_DISABLED);
      final var comment = document.getString(KEY_DISABLED);
      return new JobMeta(jobType, isRunning, isDisabled, comment, meta);
    } else {
      return new JobMeta(jobType, false, false, "", emptyMap());
    }
  }

  @Override
  public boolean createValue(final String jobType, final String key, final String value) {

    final var filter = and(eq(ID, jobType), exists(key, false));

    final var update = set(key, value);
    try {
      final var previous = collection.findOneAndUpdate(filter, update, UPSERT);
      return previous == null || previous.getString(key) == null;
    } catch (final Exception e) {
      return false;
    }
  }

  @Override
  public boolean setRunningJob(final String jobType, final String jobId) {
    return createValue(jobType, KEY_RUNNING, jobId);
  }

  @Override
  public String getRunningJob(final String jobType) {
    return getValue(jobType, KEY_RUNNING);
  }

  @Override
  public void clearRunningJob(final String jobType) {
    setValue(jobType, KEY_RUNNING, null);
  }

  @Override
  public void disable(final String jobType, final String comment) {
    setValue(jobType, KEY_DISABLED, comment != null ? comment : "");
  }

  @Override
  public void enable(final String jobType) {
    setValue(jobType, KEY_DISABLED, null);
  }

  @Override
  public String setValue(final String jobType, final String key, final String value) {
    final Document previous;
    if (value != null) {
      previous = collection.findOneAndUpdate(eq(ID, jobType), set(key, value), UPSERT);
    } else {
      previous = collection.findOneAndUpdate(eq(ID, jobType), unset(key), UPSERT);
    }
    return previous != null ? previous.getString("key") : null;
  }

  @Override
  public String getValue(final String jobType, final String key) {
    final var first = collection.find(eq(ID, jobType))
      .maxTime(mongoProperties.getDefaultReadTimeout(), TimeUnit.MILLISECONDS)
      .first();
    return first != null ? first.getString(key) : null;
  }

  @Override
  public Set<String> findAllJobTypes() {
    return stream(collection.find().maxTime(500, TimeUnit.MILLISECONDS).spliterator(), false).map(doc -> doc.getString(ID))
      .collect(toSet());
  }

  @Override
  public void deleteAll() {
    collection.deleteMany(new BasicDBObject());
  }

  @Override
  public String toString() {
    return "MongoJobMetaRepository";
  }
}
