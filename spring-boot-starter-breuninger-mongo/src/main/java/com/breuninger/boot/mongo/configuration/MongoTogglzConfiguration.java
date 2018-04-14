package com.breuninger.boot.mongo.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.user.UserProvider;

import com.breuninger.boot.mongo.togglz.MongoTogglzRepository;
import com.breuninger.boot.togglz.FeatureClassProvider;
import com.mongodb.client.MongoDatabase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass(name = "com.breuninger.boot.togglz.configuration.TogglzConfiguration")
@EnableConfigurationProperties(MongoProperties.class)
public class MongoTogglzConfiguration {

  @Bean
  StateRepository stateRepository(final MongoDatabase mongoDatabase, final FeatureClassProvider featureClassProvider,
                                  final UserProvider userProvider, final MongoProperties mongoProperties) {
    LOG.info("===============================");
    LOG.info("Using MongoTogglzRepository with " + mongoDatabase.getClass().getSimpleName() + " MongoDatabase impl.");
    LOG.info("===============================");
    return new MongoTogglzRepository(mongoDatabase, featureClassProvider, userProvider, mongoProperties);
  }
}
