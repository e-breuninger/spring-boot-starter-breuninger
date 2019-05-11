package com.breuninger.boot.jobs.repository.cleanup

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.service.JobExecutionService
import org.springframework.stereotype.Component

// TODO(BS): check and ensure it is called probably once on startup
@Component
class DeleteJobExecutionsWithoutDefinitions(
  private val jobExecutionService: JobExecutionService,
  private val jobRunnables: List<JobRunnable>
) : JobExecutionCleanupStrategy {

  override fun cleanUp() {
    val jobIdsWithDefinitions = jobRunnables.map { it.definition().jobId }.toList()
    jobExecutionService.findAllIgnoreMessages().forEach {
      if (!jobIdsWithDefinitions.contains(it.jobId))
        jobExecutionService.remove(it)
    }
  }
}
