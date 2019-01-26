package com.breuninger.boot.togglz.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import java.util.concurrent.TimeUnit

@ConfigurationProperties(prefix = "togglz", ignoreUnknownFields = true)
@Validated
class TogglzProperties {

  val cache = Cache()

  class Cache {

    var timeToLive: Long = 30
    var timeUnit = TimeUnit.SECONDS
  }

  val mongo = Mongo()

  class Mongo {
    var enabled = false
  }
}
