package com.breuninger.boot.jobs

import com.breuninger.boot.jobs.domain.JobDefinition

interface JobRunnable {

  fun definition(): JobDefinition

  fun execute(): Boolean

  fun actuatorEndpointPublicMethodName() = this::execute.name
}
