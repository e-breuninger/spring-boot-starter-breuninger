package com.breuninger.boot.jobs.repository.cleanup

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.service.JobService
import io.micrometer.core.annotation.Timed
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DeleteJobsWithoutDefinitions(
  private val jobService: JobService,
  private val jobRunnables: List<JobRunnable>
) {

  companion object {

    private const val DELETE_JOBS_WITHOUT_DEFINITIONS_CLEANUP_FIXED_RATE = 10L * 1000L
  }

  @Timed("com.breuninger.boot.jobs.repository.cleanup.DeleteJobsWithoutDefinitions.cleanUp", longTask = true)
  @Scheduled(initialDelay = 0, fixedRate = DELETE_JOBS_WITHOUT_DEFINITIONS_CLEANUP_FIXED_RATE)
  fun cleanUp() {
    jobService.findAll().forEach {
      if (!jobRunnables.map { it.definition().jobId }.toList().contains(it.id)) {
        jobService.remove(it)
      }
    }
  }
}
