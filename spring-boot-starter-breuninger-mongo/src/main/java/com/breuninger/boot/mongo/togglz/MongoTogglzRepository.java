package com.breuninger.boot.mongo.togglz;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.Map;

import org.bson.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.user.UserProvider;

import com.breuninger.boot.mongo.AbstractMongoRepository;
import com.breuninger.boot.mongo.configuration.MongoProperties;
import com.breuninger.boot.togglz.FeatureClassProvider;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnMissingBean(StateRepository.class)
public class MongoTogglzRepository extends AbstractMongoRepository<String, FeatureState> implements StateRepository {

  private static final String NAME = "_id";
  private static final String ENABLED = "enabled";
  private static final String STRATEGY = "strategy";
  private static final String PARAMETERS = "parameters";

  private final MongoCollection<Document> collection;
  private final FeatureClassProvider featureClassProvider;
  private final UserProvider userProvider;

  public MongoTogglzRepository(final MongoDatabase mongoDatabase, final FeatureClassProvider featureClassProvider,
                               final UserProvider userProvider, final MongoProperties mongoProperties) {
    super(mongoProperties);
    this.featureClassProvider = featureClassProvider;
    collection = mongoDatabase.getCollection("togglz");
    this.userProvider = userProvider;
  }

  @Override
  public FeatureState getFeatureState(final Feature feature) {
    final var featureState = findOne(feature.name());
    return featureState.orElse(null);
  }

  @Override
  public void setFeatureState(final FeatureState featureState) {
    createOrUpdate(featureState);
    LOG.info((!isEmpty(userProvider.getCurrentUser().getName()) ?
      "User '" + userProvider.getCurrentUser().getName() + "'" :
      "Unknown user") + (featureState.isEnabled() ? " enabled " : " disabled ") + "feature " + featureState.getFeature().name());
  }

  @Override
  protected MongoCollection<Document> collection() {
    return collection;
  }

  @Override
  protected String keyOf(final FeatureState value) {
    return value.getFeature().name();
  }

  @Override
  protected Document encode(final FeatureState value) {
    final var document = new Document();

    document.append(NAME, value.getFeature().name());
    document.append(ENABLED, value.isEnabled());
    document.append(STRATEGY, value.getStrategyId());
    document.append(PARAMETERS, value.getParameterMap());

    return document;
  }

  @Override
  protected FeatureState decode(final Document document) {
    final var name = document.getString(NAME);
    final var enabled = document.getBoolean(ENABLED);
    final var strategy = document.getString(STRATEGY);
    final Map<String, String> parameters = document.get(PARAMETERS, Map.class);

    final var featureState = new FeatureState(resolveEnumValue(name));
    featureState.setEnabled(enabled);
    featureState.setStrategyId(strategy);
    for (final var parameter : parameters.entrySet()) {
      featureState.setParameter(parameter.getKey(), parameter.getValue());
    }

    return featureState;
  }

  @Override
  protected void ensureIndexes() {
  }

  private Feature resolveEnumValue(final String name) {
    final Class enumType = featureClassProvider.getFeatureClass();
    return (Feature) Enum.valueOf(enumType, name);
  }
}
