package com.breuninger.boot.jobs.repository

import assertk.assertThat
import assertk.assertions.*
import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobBlockedException
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// TODO(BS): implement more tests see TODOs in repository class
abstract class AbstractJobRepositoryTest {

  @BeforeEach
  fun before() = getRepository().clear()

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

  @Test
  fun `ensure findOneRunning returns all running jobs`() {
    val jobFoo = createJob()
    val jobBar = createJob(id = JobId("bar"), runningJobExecutionId = null)
    val jobBat = createJob(id = JobId("bat"), runningJobExecutionId = JobExecutionId("bim"))

    getRepository().create(jobFoo)
    getRepository().create(jobBar)
    getRepository().create(jobBat)

    val runningJob = getRepository().findOneRunning(setOf(JobId("foo"), JobId("bar"), JobId("bat")))
    assertThat(runningJob).isIn(jobFoo, jobBat)
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

  @Test
  fun `ensure the requested state is returned if it is contained when calling findState`(){
    val job = createJob()
    getRepository().create(job)
    assertThat(getRepository().findState(JobId("foo"), "foo")).isEqualTo("bar")
    assertThat(getRepository().findState(JobId("foo"), "bat")).isEqualTo("buzz")
  }

  @Test
  fun `ensure the null is returned if the requested state is not contained when calling findState`(){
    val job = createJob()
    getRepository().create(job)
    assertThat(getRepository().findState(JobId("foo"), "bam")).isNull()
  }

  @Test
  fun `ensure that updateState adds a new state to the map if it is called with an none existing key`(){
    val job = createJob()
    getRepository().create(job)
    getRepository().updateState(JobId("foo"), "test-key", "test-value")
    assertThat(getRepository().findState(JobId("foo"), "test-key")).isEqualTo("test-value")
  }

  @Test
  fun `ensure that updateState updates an existing key correctly with the new value`(){
    val job = createJob()
    getRepository().create(job)
    getRepository().updateState(JobId("foo"), "foo", "foo-bar-buzz")
    assertThat(getRepository().findState(JobId("foo"), "foo")).isEqualTo("foo-bar-buzz")
  }

  @Test
  fun `ensure that updateState removes an existing key correctly when it is called with a null value`(){
    val job = createJob()
    getRepository().create(job)
    getRepository().updateState(JobId("foo"), "foo", null)
    assertThat(getRepository().findState(JobId("foo"), "foo")).isNull()
  }

  @Test
  fun `ensure clear works`() {
    getRepository().create(createJob())
    getRepository().create(createJob(id = JobId("bar")))
    getRepository().create(createJob(id = JobId("bat")))

    getRepository().clear()

    assertThat(getRepository().findOne(JobId("foo"))).isNull()
    assertThat(getRepository().findOne(JobId("bar"))).isNull()
    assertThat(getRepository().findOne(JobId("bat"))).isNull()
  }

  abstract fun getRepository(): JobRepository

  private fun createJob(
    id: JobId = JobId("foo"),
    runningJobExecutionId: JobExecutionId? = JobExecutionId("bar"),
    disabled: Boolean = false,
    disabledComment: String = ""
  ) = Job(id, runningJobExecutionId, disabled, disabledComment, mapOf("foo" to "bar", "bat" to "buzz"))
}
