package com.breuninger.boot.validation.autoconfiguration

import com.breuninger.boot.validation.web.GlobalErrorAttributes
import com.breuninger.boot.validation.web.GlobalErrorWebExceptionHandler
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.result.view.ViewResolver
import java.util.stream.Collectors.toList

@Configuration
@EnableConfigurationProperties(ValidationProperties::class)
@ConditionalOnProperty(prefix = "breuni.validation", name = ["enabled"], havingValue = "true")
class ValidationAutoConfiguration {

  @Bean
  fun customErrorAttributes(validationProperties: ValidationProperties, objectMapper: ObjectMapper): GlobalErrorAttributes {
    objectMapper.setDefaultPropertyInclusion(NON_ABSENT)
    return GlobalErrorAttributes(objectMapper)
  }

  @Bean
  @Order(-2)
  fun errorWebExceptionHandler(validationProperties: ValidationProperties,
                               errorAttributes: ErrorAttributes,
                               resourceProperties: ResourceProperties,
                               serverProperties: ServerProperties,
                               applicationContext: ApplicationContext,
                               viewResolversProvider: ObjectProvider<ViewResolver>,
                               serverCodecConfigurer: ServerCodecConfigurer): ErrorWebExceptionHandler {
    val exceptionHandler = GlobalErrorWebExceptionHandler(validationProperties, errorAttributes, resourceProperties,
      serverProperties.error, applicationContext)
    exceptionHandler.setViewResolvers(viewResolversProvider.orderedStream().collect(toList()))
    exceptionHandler.setMessageWriters(serverCodecConfigurer.writers)
    exceptionHandler.setMessageReaders(serverCodecConfigurer.readers)
    return exceptionHandler
  }
}
