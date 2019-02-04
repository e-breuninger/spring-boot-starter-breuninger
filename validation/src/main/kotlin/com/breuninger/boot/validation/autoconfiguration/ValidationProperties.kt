package com.breuninger.boot.validation.autoconfiguration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "validation", ignoreUnknownFields = true)
@Validated
class ValidationProperties {

  var errorMediaType = MediaType.APPLICATION_JSON_UTF8_VALUE
}
