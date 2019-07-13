package com.breuninger.boot.jobs.autoconfigure

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["mongo.enabled"], havingValue = "true")
class JobsMongoConfiguration {

  @Bean
  fun jobsMongoTemplate(mongoTemplate: MongoTemplate) = mongoTemplate
}
