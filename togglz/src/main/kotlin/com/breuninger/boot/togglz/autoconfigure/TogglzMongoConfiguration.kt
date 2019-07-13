package com.breuninger.boot.togglz.autoconfigure

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration
@ConditionalOnProperty(prefix = "breuni.togglz", name = ["mongo.enabled"], havingValue = "true")
class TogglzMongoConfiguration {

  @Bean
  fun togglzMongoTemplate(mongoTemplate: MongoTemplate) = mongoTemplate
}
