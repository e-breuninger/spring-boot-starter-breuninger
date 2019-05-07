package com.breuninger.boot.jobs.repository

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobBlockedException
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import org.hamcrest.collection.IsIterableContainingInAnyOrder
import org.junit.Assert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

abstract class AbstractJobRepositoryTest {
  abstract fun getRepository(): JobRepository

  fun createJob(
    id: JobId = JobId("foo"),
    runningJobExecutionId: JobExecutionId? = JobExecutionId("bar"),
    disabled: Boolean = false,
    disabledComment: String = "") = Job(id, runningJobExecutionId, disabled, disabledComment, emptyMap())

  @Test
  fun `ensure that inserting and finding a job works`() {
    val jobFoo = createJob()
    val jobBar = createJob(id = JobId("bar"))
    val jobBat = createJob(id = JobId("bat"))

    getRepository().create(jobFoo)
    getRepository().create(jobBar)
    getRepository().create(jobBat)

    Assertions.assertEquals(jobFoo, getRepository().findOne(JobId("foo")))
    Assertions.assertEquals(jobBar, getRepository().findOne(JobId("bar")))
    Assertions.assertEquals(jobBat, getRepository().findOne(JobId("bat")))
  }

  @Test
  fun `ensure that an already inserted job is not overridden if insert is called again for an job with the same id`() {
    val job1 = createJob()
    val job2 = createJob(disabled = true, disabledComment = "foobar")

    getRepository().create(job1)
    getRepository().create(job2)

    Assertions.assertEquals(job1, getRepository().findOne(JobId("foo")))
    Assertions.assertNotEquals(job2, getRepository().findOne(JobId("foo")))
  }

  //TODO klären mit BS: warum ist find running so wie es ist
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

    Assert.assertThat(getRepository().findAll(), IsIterableContainingInAnyOrder.containsInAnyOrder(jobFoo, jobBar, jobBat))
  }

  @Test
  fun `ensure updateDisableState updates the disabled state correctly`() {
    val jobFoo = createJob()
    val jobId = JobId("foo")
    val disableComment = "blubb"

    getRepository().create(jobFoo)

    var job = getRepository().findOne(jobId)!!
    Assertions.assertFalse(job.disabled)
    Assertions.assertEquals("", job.disableComment)


    getRepository().updateDisableState(jobId, createJob(disabled = true, disabledComment = disableComment))

    job = getRepository().findOne(jobId)!!
    Assertions.assertTrue(job.disabled)
    Assertions.assertEquals(disableComment, job.disableComment)

    getRepository().updateDisableState(jobId, createJob())

    job = getRepository().findOne(jobId)!!
    Assertions.assertFalse(job.disabled)
    Assertions.assertEquals("", job.disableComment)
  }

  @Test
  fun `ensure acquireRunLock sets the runningJobExecutionId to the correct value`() {
    val jobFoo = createJob(runningJobExecutionId = null)
    val jobExecutionId = JobExecutionId("bar")
    val jobId = JobId("foo")

    getRepository().create(jobFoo)

    getRepository().acquireRunLock(jobId, jobExecutionId)

    Assertions.assertEquals(jobExecutionId, getRepository().findOne(jobId)!!.runningJobExecutionId)
  }

  @Test
  fun `ensure acquireRunLock does not change the runningJobExecutionId if it is already set`() {
    val jobFoo = createJob()
    val jobExecutionId = JobExecutionId("bat")
    val jobId = JobId("foo")

    getRepository().create(jobFoo)
    getRepository().acquireRunLock(jobId, jobExecutionId)

    Assertions.assertNotEquals(jobExecutionId, getRepository().findOne(jobId)!!.runningJobExecutionId)
  }

  @Test
  fun `ensure releaseRunLock removes the runningJobExecutionId if it is called with the same jobExecutionId`() {
    val jobFoo = createJob()
    val jobExecutionId = JobExecutionId("bar")
    val jobId = JobId("foo")

    getRepository().create(jobFoo)
    getRepository().releaseRunLock(jobId, jobExecutionId)

    Assertions.assertNull(getRepository().findOne(jobId)!!.runningJobExecutionId)
  }

  @Test
  fun `ensure releaseRunLock throws an Exception if it is called with the wrong jobExecutionId`() {
    val jobFoo = createJob()

    getRepository().create(jobFoo)

    Assertions.assertThrows(JobBlockedException::class.java, { getRepository().releaseRunLock(JobId("foo"), JobExecutionId("bat")) })
  }
}
