package com.breuninger.boot.mongo;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import static com.breuninger.boot.mongo.UpdateIfMatchResult.CONCURRENTLY_MODIFIED;
import static com.breuninger.boot.mongo.UpdateIfMatchResult.NOT_FOUND;
import static com.breuninger.boot.mongo.UpdateIfMatchResult.OK;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.ReturnDocument.AFTER;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.bson.BsonDocument;
import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.UpdateOptions;

import com.breuninger.boot.mongo.configuration.MongoProperties;

public abstract class AbstractMongoRepository<K, V> {

  public static final String ID = "_id";
  public static final String ETAG = "etag";

  private static final boolean DISABLE_PARALLEL_STREAM_PROCESSING = false;
  protected final MongoProperties mongoProperties;

  public AbstractMongoRepository(final MongoProperties mongoProperties) {
    this.mongoProperties = mongoProperties;
  }

  protected static <T> Stream<T> toStream(final Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), DISABLE_PARALLEL_STREAM_PROCESSING);
  }

  @PostConstruct
  public void postConstruct() {
    ensureIndexes();
  }

  public Optional<V> findOne(final K key) {
    return findOne(key, mongoProperties.getDefaultReadTimeout(), TimeUnit.MILLISECONDS);
  }

  public Optional<V> findOne(final K key, final long maxTime, final TimeUnit timeUnit) {
    return ofNullable(collection().find(byId(key)).maxTime(maxTime, timeUnit).map(this::decode).first());
  }

  public Stream<V> findAllAsStream() {
    return findAllAsStream(mongoProperties.getDefaultReadTimeout(), TimeUnit.MILLISECONDS);
  }

  public Stream<V> findAllAsStream(final long maxTime, final TimeUnit timeUnit) {
    return toStream(collection().find().maxTime(maxTime, timeUnit)).map(this::decode);
  }

  public List<V> findAll() {
    return findAll(mongoProperties.getDefaultReadTimeout(), TimeUnit.MILLISECONDS);
  }

  public List<V> findAll(final long maxTime, final TimeUnit timeUnit) {
    return findAllAsStream(maxTime, timeUnit).collect(toList());
  }

  public Stream<V> findAllAsStream(final int skip, final int limit) {
    return findAllAsStream(skip, limit, mongoProperties.getDefaultReadTimeout(), TimeUnit.MILLISECONDS);
  }

  public Stream<V> findAllAsStream(final int skip, final int limit, final long maxTime, final TimeUnit timeUnit) {
    return toStream(getFindIterable(skip, limit).maxTime(maxTime, timeUnit)).map(this::decode);
  }

  private FindIterable<Document> getFindIterable(final int skip, final int limit) {
    return collection().find().skip(skip).limit(limit);
  }

  public List<V> findAll(final int skip, final int limit) {
    return findAll(skip, limit, mongoProperties.getDefaultReadTimeout(), TimeUnit.MILLISECONDS);
  }

  public List<V> findAll(final int skip, final int limit, final long maxTime, final TimeUnit timeUnit) {
    return findAllAsStream(skip, limit, maxTime, timeUnit).collect(toList());
  }

  public V createOrUpdate(final V value) {
    return createOrUpdate(value, mongoProperties.getDefaultWriteTimeout(), TimeUnit.MILLISECONDS);
  }

  public V createOrUpdate(final V value, final long maxTime, final TimeUnit timeUnit) {
    final var doc = encode(value);
    collectionWithWriteTimeout(maxTime, timeUnit).replaceOne(byId(keyOf(value)), doc, new UpdateOptions().upsert(true));
    return decode(doc);
  }

  protected MongoCollection<Document> collectionWithWriteTimeout(final long maxTime, final TimeUnit timeUnit) {
    return collection().withWriteConcern(collection().getWriteConcern().withWTimeout(maxTime, timeUnit));
  }

  public V create(final V value) {
    return create(value, mongoProperties.getDefaultWriteTimeout(), TimeUnit.MILLISECONDS);
  }

  public V create(final V value, final long maxTime, final TimeUnit timeUnit) {
    final var key = keyOf(value);
    if (key != null) {
      final var doc = encode(value);
      collectionWithWriteTimeout(maxTime, timeUnit).insertOne(doc);
      return decode(doc);
    } else {
      throw new NullPointerException("Key must not be null");
    }
  }

  public boolean update(final V value) {
    return update(value, mongoProperties.getDefaultWriteTimeout(), TimeUnit.MILLISECONDS);
  }

  public boolean update(final V value, final long maxTime, final TimeUnit timeUnit) {
    final var key = keyOf(value);
    if (key != null) {
      return collectionWithWriteTimeout(maxTime, timeUnit).replaceOne(byId(key), encode(value)).getModifiedCount() == 1;
    } else {
      throw new IllegalArgumentException("Key must not be null");
    }
  }

  public UpdateIfMatchResult updateIfMatch(final V value, final String eTag) {
    return updateIfMatch(value, eTag, mongoProperties.getDefaultWriteTimeout(), TimeUnit.MILLISECONDS);
  }

  public UpdateIfMatchResult updateIfMatch(final V value, final String eTag, final long maxTime, final TimeUnit timeUnit) {
    final var key = keyOf(value);
    if (key != null) {
      final var query = and(eq(AbstractMongoRepository.ID, key), eq(ETAG, eTag));

      final var updatedETaggable = collectionWithWriteTimeout(maxTime, timeUnit).findOneAndReplace(query, encode(value),
        new FindOneAndReplaceOptions().returnDocument(AFTER));
      if (isNull(updatedETaggable)) {
        final var documentExists =
          collection().count(eq(AbstractMongoRepository.ID, key), new CountOptions().maxTime(maxTime, timeUnit)) != 0;
        if (documentExists) {
          return CONCURRENTLY_MODIFIED;
        }

        return NOT_FOUND;
      }

      return OK;
    } else {
      throw new IllegalArgumentException("Key must not be null");
    }
  }

  public long size() {
    return size(mongoProperties.getDefaultReadTimeout(), TimeUnit.MILLISECONDS);
  }

  public long size(final long maxTime, final TimeUnit timeUnit) {
    return collection().count(new BsonDocument(), new CountOptions().maxTime(maxTime, timeUnit));
  }

  public void delete(final K key) {
    delete(key, mongoProperties.getDefaultWriteTimeout(), TimeUnit.MILLISECONDS);
  }

  public void delete(final K key, final long maxTime, final TimeUnit timeUnit) {
    collectionWithWriteTimeout(maxTime, timeUnit).deleteOne(byId(key));
  }

  public void deleteAll() {
    deleteAll(mongoProperties.getDefaultWriteTimeout(), TimeUnit.MILLISECONDS);
  }

  public void deleteAll(final long maxTime, final TimeUnit timeUnit) {
    collectionWithWriteTimeout(maxTime, timeUnit).deleteMany(matchAll());
  }

  protected Document byId(final K key) {
    if (key != null) {
      return new Document(ID, key.toString());
    } else {
      throw new NullPointerException("Key must not be null");
    }
  }

  protected Document matchAll() {
    return new Document();
  }

  protected abstract MongoCollection<Document> collection();

  protected abstract K keyOf(final V value);

  protected abstract Document encode(final V value);

  protected abstract V decode(final Document document);

  protected abstract void ensureIndexes();
}
