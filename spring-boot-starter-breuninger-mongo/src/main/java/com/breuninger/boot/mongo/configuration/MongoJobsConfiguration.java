package com.breuninger.boot.mongo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.breuninger.boot.jobs.repository.JobMetaRepository;
import com.breuninger.boot.jobs.repository.JobRepository;
import com.breuninger.boot.mongo.jobs.MongoJobMetaRepository;
import com.breuninger.boot.mongo.jobs.MongoJobRepository;
import com.mongodb.client.MongoDatabase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass(name = "com.breuninger.boot.jobs.configuration.JobsConfiguration")
@EnableConfigurationProperties(MongoProperties.class)
public class MongoJobsConfiguration {

  @Bean
  public JobRepository jobRepository(final MongoDatabase mongoDatabase,
                                     @Value("${breuninger.jobs.collection.jobinfo:jobinfo}") final String collectionName,
                                     final MongoProperties mongoProperties) {
    LOG.info("===============================");
    LOG.info("Using MongoJobRepository with {} MongoDatabase impl.", mongoDatabase.getClass().getSimpleName());
    LOG.info("===============================");
    return new MongoJobRepository(mongoDatabase, collectionName, mongoProperties);
  }

  @Bean
  public JobMetaRepository jobMetaRepository(final MongoDatabase mongoDatabase,
                                             @Value("${breuninger.jobs.collection.jobmeta:jobmeta}") final String collectionName,
                                             final MongoProperties mongoProperties) {
    LOG.info("===============================");
    LOG.info("Using MongoJobMetaRepository with {} MongoDatabase impl.", mongoDatabase.getClass().getSimpleName());
    LOG.info("===============================");
    return new MongoJobMetaRepository(mongoDatabase, collectionName, mongoProperties);
  }
}
