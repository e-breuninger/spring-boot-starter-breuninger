package com.breuninger.boot.jobs.repository.mongo

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobBlockedException
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.domain.UnableToRemoveException
import com.breuninger.boot.jobs.repository.JobRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.Update.update
import org.springframework.data.mongodb.core.updateFirst
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["mongo.enabled"], havingValue = "true")
class MongoJobRepository(private val jobsMongoTemplate: MongoTemplate) : JobRepository {

  companion object {

    private val LOG: Logger = LoggerFactory.getLogger(MongoJobRepository::class.java)
  }

  override fun findOne(jobId: JobId) = jobsMongoTemplate.findById<Job>(jobId)

  override fun findOneRunning(jobIds: Set<JobId>) =
    jobsMongoTemplate.findOne<Job>(query(where("_id").`in`(jobIds).and(Job::runningJobExecutionId.name).exists(true)))

  override fun findAll(): List<Job> = jobsMongoTemplate.findAll(Job::class.java)

  override fun create(job: Job) = try {
    jobsMongoTemplate.insert(job)
    Unit
  } catch (exception: Exception) {
    LOG.info("Job already created")
  }

  override fun updateDisableState(jobId: JobId, job: Job) =
    jobsMongoTemplate.findAndModify(query(where("_id").`is`(jobId)).limit(1),
      update(Job::disabled.name, job.disabled)
        .set(Job::disableComment.name, job.disableComment),
      FindAndModifyOptions().returnNew(true),
      Job::class.java)

  override fun acquireRunLock(jobId: JobId, jobExecutionId: JobExecutionId) =
    jobsMongoTemplate.findAndModify(query(where("_id").`is`(jobId).and(Job::runningJobExecutionId.name).exists(false)),
      update(Job::runningJobExecutionId.name, jobExecutionId), Job::class.java)

  @Throws(JobBlockedException::class)
  override fun releaseRunLock(jobId: JobId, jobExecutionId: JobExecutionId) {
    if (jobsMongoTemplate.updateFirst<Job>(
        query(where("_id").`is`(jobId).and(Job::runningJobExecutionId.name).`is`(jobExecutionId)),
        Update().unset(Job::runningJobExecutionId.name))
        .modifiedCount != 1L)
      throw JobBlockedException("Tried to release runLock of $jobId but different execution was running")
  }

  override fun findState(jobId: JobId, key: String): String? {
    val query = query(where("_id").`is`(jobId))
    query.fields().include("${Job::state.name}.$key")
    val jobCollection = (Job::class.annotations.first { it is Document } as Document).collection
    return jobsMongoTemplate.findOne<Map<String, Map<String, String>>>(query, jobCollection)?.let {
      it[Job::state.name]?.get(key)
    }
  }

  override fun updateState(jobId: JobId, key: String, value: String?) {
    val stateKey = "${Job::state.name}.$key"
    jobsMongoTemplate.updateFirst<Job>(query(where("_id").`is`(jobId)), value?.let { update(stateKey, it) } ?: let {
      Update().unset(stateKey)
    })
  }

  override fun remove(job: Job) {
    val deleteResult = jobsMongoTemplate.remove(job)
    if (deleteResult.deletedCount != 1L) {
      throw UnableToRemoveException("Unable to remove $job")
    }
  }

  override fun drop() = jobsMongoTemplate.dropCollection<Job>()
}
