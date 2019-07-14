package com.breuninger.boot.togglz.autoconfigure

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.MongoConverter

@Configuration
@ConditionalOnProperty(prefix = "breuni.togglz", name = ["mongo.enabled"], havingValue = "true")
class TogglzMongoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  fun togglzMongoTemplate(mongoDbFactory: MongoDbFactory, converter: MongoConverter) = MongoTemplate(mongoDbFactory, converter)
}
