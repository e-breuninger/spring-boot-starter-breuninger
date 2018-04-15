package com.breuninger.boot.mongo.configuration;

import static com.mongodb.MongoCredential.createCredential;

import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfiguration {

  private MongoCredential getCredential(final MongoProperties mongoProperties) {
    return createCredential(mongoProperties.getUser(), getAuthenticationDb(mongoProperties),
      mongoProperties.getPassword().toCharArray());
  }

  private static boolean useUnauthorizedConnection(final MongoProperties mongoProperties) {
    return mongoProperties.getUser().isEmpty() || mongoProperties.getPassword().isEmpty();
  }

  private static String getAuthenticationDb(final MongoProperties mongoProperties) {
    final var authenticationDb = mongoProperties.getAuthenticationDb();
    if (authenticationDb != null && !authenticationDb.isEmpty()) {
      return authenticationDb;
    }
    return mongoProperties.getDb();
  }

  private MongoClientOptions getOptions(final MongoProperties mongoProperties) {
    return mongoProperties.toMongoClientOptions(codecRegistry());
  }

  @Bean
  @ConditionalOnMissingBean(CodecRegistry.class)
  public CodecRegistry codecRegistry() {
    return MongoClient.getDefaultCodecRegistry();
  }

  @Bean
  @Primary
  @ConditionalOnMissingBean(name = "mongoClient", value = MongoClient.class)
  public MongoClient mongoClient(final MongoProperties mongoProperties) {
    LOG.info("Creating MongoClient");
    if (useUnauthorizedConnection(mongoProperties)) {
      return new MongoClient(mongoProperties.getServers(), getOptions(mongoProperties));
    }
    return new MongoClient(mongoProperties.getServers(), getCredential(mongoProperties), getOptions(mongoProperties));
  }

  @Bean
  @Primary
  @ConditionalOnMissingBean(name = "mongoDatabase", value = MongoDatabase.class)
  public MongoDatabase mongoDatabase(final MongoClient mongoClient, final MongoProperties mongoProperties) {
    return mongoClient.getDatabase(mongoProperties.getDb());
  }
}
