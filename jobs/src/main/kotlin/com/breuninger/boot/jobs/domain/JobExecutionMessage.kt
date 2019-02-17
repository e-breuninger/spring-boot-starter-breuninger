package com.breuninger.boot.jobs.domain

import java.time.Instant

data class JobExecutionMessage(
  val timestamp: Instant,
  val level: Level,
  val message: String
) {

  enum class Level {
    INFO, WARNING, ERROR
  }
}
