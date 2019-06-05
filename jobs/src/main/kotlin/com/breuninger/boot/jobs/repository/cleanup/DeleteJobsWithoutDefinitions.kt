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
) : JobExecutionCleanupStrategy {

  companion object {

    private const val DELETE_JOBS_WITHOUT_DEFINITIONS_CLEANUP_INTERVAL = 10L * 60L * 1000L
  }

  @Timed("com.breuninger.boot.jobs.repository.cleanup.DeleteJobsWithoutDefinitions.cleanUp", longTask = true)
  @Scheduled(fixedRate = DELETE_JOBS_WITHOUT_DEFINITIONS_CLEANUP_INTERVAL)
  override fun cleanUp() {
    val jobIdsWithDefinitions = jobRunnables.map { it.definition().jobId }.toList()
    jobService.findAll().forEach {
      if (!jobIdsWithDefinitions.contains(it.id))
        jobService.remove(it)
    }
  }
}
