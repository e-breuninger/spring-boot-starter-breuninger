package com.breuninger.boot.jobs.repository.cleanup

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.service.JobService
import org.springframework.stereotype.Component

// TODO(BS): check and ensure it is called probably once on startup
@Component
class DeleteJobsWithoutDefinitions(
  private val jobService: JobService,
  private val jobRunnables: List<JobRunnable>
) : JobExecutionCleanupStrategy {

  override fun cleanUp() {
    val jobIdsWithDefinitions = jobRunnables.map { it.definition().jobId }.toList()
    jobService.findAll().forEach {
      if (!jobIdsWithDefinitions.contains(it.id))
        jobService.remove(it)
    }
  }
}
