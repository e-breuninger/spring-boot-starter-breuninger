package com.breuninger.boot.jobs.domain

import java.util.UUID

data class JobExecutionId(val value: String) {
  constructor() : this(UUID.randomUUID().toString())
}
