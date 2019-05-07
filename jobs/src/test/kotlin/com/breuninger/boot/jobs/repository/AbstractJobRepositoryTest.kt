package com.breuninger.boot.jobs.repository

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobBlockedException
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder
import org.hamcrest.junit.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// TODO(BS): implement more tests see TODOs in repository class
abstract class AbstractJobRepositoryTest {

  @Test
  fun `ensure that inserting and finding a job works`() {
    val jobFoo = createJob()
    val jobBar = createJob(id = JobId("bar"))
    val jobBat = createJob(id = JobId("bat"))

    getRepository().create(jobFoo)
    getRepository().create(jobBar)
    getRepository().create(jobBat)

    assertEquals(jobFoo, getRepository().findOne(JobId("foo")))
    assertEquals(jobBar, getRepository().findOne(JobId("bar")))
    assertEquals(jobBat, getRepository().findOne(JobId("bat")))
  }

  @Test
  fun `ensure that an already inserted job is not overridden if insert is called again for an job with the same id`() {
    val job1 = createJob()
    val job2 = createJob(disabled = true, disabledComment = "foobar")

    getRepository().create(job1)
    getRepository().create(job2)

    assertEquals(job1, getRepository().findOne(JobId("foo")))
    assertNotEquals(job2, getRepository().findOne(JobId("foo")))
  }

  // TODO(KA): implement test :)
  fun `ensure findRunning returns all running jobs`() {
    val jobFoo = createJob()
    val jobBar = createJob(id = JobId("bar"), runningJobExecutionId = null)
    val jobBat = createJob(id = JobId("bat"), runningJobExecutionId = JobExecutionId("bim"))

    getRepository().create(jobFoo)
    getRepository().create(jobBar)
    getRepository().create(jobBat)
  }

  @Test
  fun `ensure findAll returns all added jobs`() {
    val jobFoo = createJob()
    val jobBar = createJob(id = JobId("bar"))
    val jobBat = createJob(id = JobId("bat"))

    getRepository().create(jobFoo)
    getRepository().create(jobBar)
    getRepository().create(jobBat)

    assertThat(getRepository().findAll(), containsInAnyOrder(jobFoo, jobBar, jobBat))
  }

  @Test
  fun `ensure updateDisableState updates the disabled state correctly`() {
    val jobFoo = createJob()
    val jobId = JobId("foo")
    val disableComment = "blubb"

    getRepository().create(jobFoo)

    var job = getRepository().findOne(jobId)!!
    assertFalse(job.disabled)
    assertEquals("", job.disableComment)

    getRepository().updateDisableState(jobId, createJob(disabled = true, disabledComment = disableComment))

    job = getRepository().findOne(jobId)!!
    assertTrue(job.disabled)
    assertEquals(disableComment, job.disableComment)

    getRepository().updateDisableState(jobId, createJob())

    job = getRepository().findOne(jobId)!!
    assertFalse(job.disabled)
    assertEquals("", job.disableComment)
  }

  @Test
  fun `ensure acquireRunLock sets the runningJobExecutionId to the correct value`() {
    val jobFoo = createJob(runningJobExecutionId = null)
    val jobExecutionId = JobExecutionId("bar")
    val jobId = JobId("foo")

    getRepository().create(jobFoo)

    getRepository().acquireRunLock(jobId, jobExecutionId)

    assertEquals(jobExecutionId, getRepository().findOne(jobId)!!.runningJobExecutionId)
  }

  @Test
  fun `ensure acquireRunLock does not change the runningJobExecutionId if it is already set`() {
    val jobFoo = createJob()
    val jobExecutionId = JobExecutionId("bat")
    val jobId = JobId("foo")

    getRepository().create(jobFoo)
    getRepository().acquireRunLock(jobId, jobExecutionId)

    assertNotEquals(jobExecutionId, getRepository().findOne(jobId)!!.runningJobExecutionId)
  }

  @Test
  fun `ensure releaseRunLock removes the runningJobExecutionId if it is called with the same jobExecutionId`() {
    val jobFoo = createJob()
    val jobExecutionId = JobExecutionId("bar")
    val jobId = JobId("foo")

    getRepository().create(jobFoo)
    getRepository().releaseRunLock(jobId, jobExecutionId)

    assertNull(getRepository().findOne(jobId)!!.runningJobExecutionId)
  }

  @Test
  fun `ensure releaseRunLock throws an Exception if it is called with the wrong jobExecutionId`() {
    val jobFoo = createJob()

    getRepository().create(jobFoo)

    assertThrows(JobBlockedException::class.java) {
      getRepository().releaseRunLock(JobId("foo"), JobExecutionId("bat"))
    }
  }

  abstract fun getRepository(): JobRepository

  private fun createJob(
    id: JobId = JobId("foo"),
    runningJobExecutionId: JobExecutionId? = JobExecutionId("bar"),
    disabled: Boolean = false,
    disabledComment: String = ""
  ) = Job(id, runningJobExecutionId, disabled, disabledComment, emptyMap())
}
