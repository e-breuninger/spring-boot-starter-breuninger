package com.breuninger.boot.jobs.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.Min

@ConfigurationProperties(prefix = "breuni.jobs")
@Validated
class JobsProperties {

  var enabled = false

  @field:Min(1)
  var threadCount = 10

  val mongo = Mongo()

  class Mongo {

    var enabled = false
  }

  val cleanup = Cleanup()

  class Cleanup {

    @field:Min(1)
    var numberOfJobExecutionsToKeep = 100

    @field:Min(1)
    var numberOfSkippedJobExecutionsToKeep = 10

    @field:Min(30)
    var killDeadJobExecutionsAfterSeconds = 30
  }
}
