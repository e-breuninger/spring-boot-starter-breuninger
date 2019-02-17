package com.breuninger.boot.jobs

import com.breuninger.boot.jobs.domain.JobBlockedException
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobMarker.jobMarkerFor
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent.State.KEEP_ALIVE
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent.State.RESTART
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent.State.SKIPPED
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent.State.START
import com.breuninger.boot.jobs.eventbus.domain.JobExecutionStateChangedEvent.State.STOP
import com.breuninger.boot.jobs.service.JobService
import io.micrometer.core.aop.TimedAspect.DEFAULT_METRIC_NAME
import io.micrometer.core.instrument.LongTaskTimer
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.slf4j.MDC
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.support.ScheduledMethodRunnable
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit.SECONDS

class JobExecutor(
  private val delegate: JobRunnable,
  private val jobService: JobService,
  private val eventPublisher: ApplicationEventPublisher,
  private val scheduledExecutorService: ScheduledExecutorService,
  private val meterRegistry: MeterRegistry
) : ScheduledMethodRunnable(delegate, delegate.actuatorEndpointPublicMethodName()) {

  companion object {

    private const val PING_PERIOD = 20L

    val LOG: Logger = getLogger(JobExecutor::class.java)
  }

  private val jobRunnable = wrapWithLongTaskTimer(wrapWithTimer(delegate))
  private val definition = jobRunnable.definition()
  private val jobId = definition.jobId

  private val jobMarker = jobMarkerFor(jobId)

  private var pingJob: ScheduledFuture<*>? = null

  override fun run() {
    try {
      val jobExecutionId = JobExecutionId()
      jobService.acquireRunLock(jobId, jobExecutionId)
      try {
        start(jobExecutionId)
        executeAndRestart(jobExecutionId, definition.restarts, definition.restartDelay)
      } catch (exception: Exception) {
        error(jobExecutionId, exception)
      } finally {
        stop(jobExecutionId)
      }
    } catch (jobBlockedException: JobBlockedException) {
      LOG.info(jobBlockedException.message)
    } catch (ignored: Exception) {
    }
  }

  @Synchronized
  private fun start(jobExecutionId: JobExecutionId) {
    MDC.put("job_id_value", jobId.value)
    MDC.put("job_execution_id_value", jobExecutionId.value)
    LOG.info(jobMarker, "JobRunnable started $jobExecutionId")
    eventPublisher.publishEvent(JobExecutionStateChangedEvent(jobRunnable, jobExecutionId, START))
    pingJob = scheduledExecutorService.scheduleAtFixedRate({ ping(jobExecutionId) }, PING_PERIOD, PING_PERIOD, SECONDS)
  }

  @Synchronized
  private fun executeAndRestart(jobExecutionId: JobExecutionId, restarts: Int, restartDelay: Duration?) {
    try {
      val executed = jobRunnable.execute()
      if (!executed) {
        eventPublisher.publishEvent(JobExecutionStateChangedEvent(jobRunnable, jobExecutionId, SKIPPED))
      }
    } catch (exception: Exception) {
      if (restarts > 0) {
        LOG.warn("Restarting jobRunnable because of an exception caught during execution: ${exception.message}")
        eventPublisher.publishEvent(JobExecutionStateChangedEvent(jobRunnable, jobExecutionId, RESTART))
        restartDelay?.let { sleep(it) }
        executeAndRestart(jobExecutionId, restarts - 1, restartDelay)
      } else {
        error(jobExecutionId, exception)
      }
    }
  }

  private fun sleep(duration: Duration) {
    try {
      Thread.sleep(duration.toMillis())
    } catch (interruptedException: InterruptedException) {
      LOG.error(jobMarker, "InterruptedException", interruptedException)
    }
  }

  private fun ping(jobExecutionId: JobExecutionId) {
    try {
      eventPublisher.publishEvent(JobExecutionStateChangedEvent(jobRunnable, jobExecutionId, KEEP_ALIVE))
    } catch (exception: Exception) {
      LOG.error(jobMarker, "Fatal error while pinging $jobId ($jobExecutionId)", exception)
    }
  }

  @Synchronized
  private fun stop(jobExecutionId: JobExecutionId) {
    pingJob?.cancel(false)
    try {
      LOG.info(jobMarker, "JobRunnable stopped $jobId ($jobExecutionId)")
      eventPublisher.publishEvent(JobExecutionStateChangedEvent(jobRunnable, jobExecutionId, STOP))
    } finally {
      MDC.clear()
    }
  }

  @Synchronized
  private fun error(jobExecutionId: JobExecutionId, exception: Exception) {
    LOG.error(jobMarker,
      "Fatal error in jobRunnable $jobId ($jobExecutionId) - ${exception.javaClass.name}: ${exception.message}", exception)
  }

  private fun wrapWithTimer(jobRunnableToTime: JobRunnable) = object : JobRunnable {

    override fun definition() = jobRunnableToTime.definition()

    override fun execute() = definition().timer?.let {
      val startTimer = Timer.start(meterRegistry)
      try {
        return jobRunnableToTime.execute()
      } finally {
        try {
          val stopTimer = Timer.builder(if (it.name.isEmpty()) DEFAULT_METRIC_NAME else it.name)
            .tag("class", delegate.javaClass.name)
            .tag("method", delegate.actuatorEndpointPublicMethodName())
            .tag("job_id", definition().jobId.value)
            .description(if (it.description.isEmpty()) null else it.description)
            .tags(*it.extraTags)
            .publishPercentileHistogram(it.histogram)
          if (it.percentiles.isNotEmpty()) {
            stopTimer.publishPercentiles(*it.percentiles)
          }
          startTimer.stop(stopTimer.register(meterRegistry))
        } catch (ignored: Exception) {
        }
      }
    } ?: jobRunnableToTime.execute()
  }

  private fun wrapWithLongTaskTimer(jobRunnableToTime: JobRunnable) = object : JobRunnable {

    override fun definition() = jobRunnableToTime.definition()

    override fun execute() = definition().timer?.let {
      if(it.longTask) {
        val longTaskTimer = LongTaskTimer.builder("${if (it.name.isEmpty()) DEFAULT_METRIC_NAME else it.name}.longTask")
          .tag("class", delegate.javaClass.name)
          .tag("method", delegate.actuatorEndpointPublicMethodName())
          .tag("job_id", definition().jobId.value)
          .description(if (it.description.isEmpty()) null else it.description)
          .tags(*it.extraTags)
          .register(meterRegistry)
          .start()
        try {
          return jobRunnableToTime.execute()
        } finally {
          try {
            longTaskTimer.stop()
          } catch (ignored: Exception) {
          }
        }
      }
      else {
        jobRunnableToTime.execute()
      }
    } ?: jobRunnableToTime.execute()
  }
}
