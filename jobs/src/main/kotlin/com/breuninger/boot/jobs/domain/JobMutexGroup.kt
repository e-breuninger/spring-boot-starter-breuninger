package com.breuninger.boot.jobs.domain

import java.util.Arrays.asList
import java.util.HashSet

class JobMutexGroup(
  val name: String,
  vararg jobIds: JobId
) {

  val jobIds = object : HashSet<JobId>() {
    init {
      addAll(asList(*jobIds))
    }
  }
}
