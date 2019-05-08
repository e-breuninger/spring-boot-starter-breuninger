package com.breuninger.boot.jobs.repository

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobBlockedException
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
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

    assertThat(getRepository().findOne(JobId("foo"))).isEqualTo(jobFoo)
    assertThat(getRepository().findOne(JobId("bar"))).isEqualTo(jobBar)
    assertThat(getRepository().findOne(JobId("bat"))).isEqualTo(jobBat)
  }

  @Test
  fun `ensure that an already inserted job is not overridden if insert is called again for an job with the same id`() {
    val job1 = createJob()
    val job2 = createJob(disabled = true, disabledComment = "foobar")

    getRepository().create(job1)
    getRepository().create(job2)

    assertThat(getRepository().findOne(JobId("foo"))).isEqualTo(job1)
    assertThat(getRepository().findOne(JobId("foo"))).isNotEqualTo(job2)
  }

  // TODO(KA): implement test :)
  fun `ensure findOneRunning returns all running jobs`() {
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

    assertThat(getRepository().findAll()).containsAll(jobFoo, jobBar, jobBat)
  }

  @Test
  fun `ensure updateDisableState updates the disabled state correctly`() {
    val jobFoo = createJob()
    val jobId = JobId("foo")
    val disableComment = "blubb"

    getRepository().create(jobFoo)

    var job = getRepository().findOne(jobId)!!
    assertThat(job.disabled).isFalse()
    assertThat(job.disableComment).isEqualTo("")

    getRepository().updateDisableState(jobId, createJob(disabled = true, disabledComment = disableComment))

    job = getRepository().findOne(jobId)!!
    assertThat(job.disabled).isTrue()
    assertThat(job.disableComment).isEqualTo(disableComment)

    getRepository().updateDisableState(jobId, createJob())

    job = getRepository().findOne(jobId)!!
    assertThat(job.disabled).isFalse()
    assertThat(job.disableComment).isEqualTo("")
  }

  @Test
  fun `ensure acquireRunLock sets the runningJobExecutionId to the correct value`() {
    val jobFoo = createJob(runningJobExecutionId = null)
    val jobExecutionId = JobExecutionId("bar")
    val jobId = JobId("foo")

    getRepository().create(jobFoo)

    getRepository().acquireRunLock(jobId, jobExecutionId)

    assertThat(getRepository().findOne(jobId)!!.runningJobExecutionId).isEqualTo(jobExecutionId)
  }

  @Test
  fun `ensure acquireRunLock does not change the runningJobExecutionId if it is already set`() {
    val jobFoo = createJob()
    val jobExecutionId = JobExecutionId("bat")
    val jobId = JobId("foo")

    getRepository().create(jobFoo)
    getRepository().acquireRunLock(jobId, jobExecutionId)

    assertThat(getRepository().findOne(jobId)!!.runningJobExecutionId).isNotEqualTo(jobExecutionId)
  }

  @Test
  fun `ensure releaseRunLock removes the runningJobExecutionId if it is called with the same jobExecutionId`() {
    val jobFoo = createJob()
    val jobExecutionId = JobExecutionId("bar")
    val jobId = JobId("foo")

    getRepository().create(jobFoo)
    getRepository().releaseRunLock(jobId, jobExecutionId)

    assertThat(getRepository().findOne(jobId)!!.runningJobExecutionId).isNull()
  }

  @Test
  fun `ensure releaseRunLock throws an Exception if it is called with the wrong jobExecutionId`() {
    val jobFoo = createJob()

    getRepository().create(jobFoo)
    assertThat { getRepository().releaseRunLock(JobId("foo"), JobExecutionId("bat")) }.thrownError { hasClass(JobBlockedException::class) }
  }

  abstract fun getRepository(): JobRepository

  private fun createJob(
    id: JobId = JobId("foo"),
    runningJobExecutionId: JobExecutionId? = JobExecutionId("bar"),
    disabled: Boolean = false,
    disabledComment: String = ""
  ) = Job(id, runningJobExecutionId, disabled, disabledComment, emptyMap())
}
