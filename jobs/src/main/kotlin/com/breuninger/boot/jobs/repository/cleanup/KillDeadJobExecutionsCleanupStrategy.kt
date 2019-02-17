package com.breuninger.boot.jobs.repository.cleanup

import com.breuninger.boot.jobs.autoconfigure.JobsProperties
import com.breuninger.boot.jobs.service.JobExecutionService
import io.micrometer.core.annotation.Timed
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant.now

@Component
class KillDeadJobExecutionsCleanupStrategy(
  private val jobExecutionService: JobExecutionService,
  private val jobsProperties: JobsProperties
) : JobExecutionCleanupStrategy {

  companion object {

    private const val KILL_DEAD_JOB_EXECUTIONS_CLEANUP_INTERVAL = 1L * 60L * 1000L

    val LOG: Logger = LoggerFactory.getLogger(KillDeadJobExecutionsCleanupStrategy::class.java)
  }

  @Timed("com.breuninger.boot.jobs.repository.cleanup.KillDeadJobExecutionsCleanupStrategy.cleanUp", longTask = true)
  @Scheduled(fixedRate = KILL_DEAD_JOB_EXECUTIONS_CLEANUP_INTERVAL)
  override fun cleanUp() {
    val killDeadJobExecutionAt = now().minusSeconds(jobsProperties.cleanup.killDeadJobExecutionsAfterSeconds.toLong())
    LOG.info("Looking for job executions older than $killDeadJobExecutionAt ")
    jobExecutionService.findAllWithoutMessages()
      .filter { it.hasNotStopped() && it.lastUpdated.isBefore(killDeadJobExecutionAt) }
      .forEach {
        jobExecutionService.markDead(it.id)
        jobExecutionService.stop(it.jobId, it.id)
      }
  }
}
