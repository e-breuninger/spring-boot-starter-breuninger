package com.breuninger.boot.jobs.repository.mongo

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobBlockedException
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.Update.update
import org.springframework.data.mongodb.core.updateFirst
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["mongo.enabled"], havingValue = "true")
class MongoJobRepository(private val mongoTemplate: MongoTemplate) : JobRepository {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(MongoJobRepository::class.java)
  }

  override fun findOne(jobId: JobId) = mongoTemplate.findById<Job>(jobId)

  override fun findRunning(jobIds: Set<JobId>) =
    mongoTemplate.findOne<Job>(query(where("_id").`in`(jobIds).and(Job::runningJobExecutionId.name).exists(true)))

  override fun insert(job: Job) {
    try {
      mongoTemplate.insert(job)
    } catch (exception: Exception) {
      LOG.info("Job already created")
    }
  }

  override fun acquireRunLock(jobId: JobId, jobExecutionId: JobExecutionId) =
    mongoTemplate.findAndModify(query(where("_id").`is`(jobId).and(Job::runningJobExecutionId.name).exists(false)),
      update(Job::runningJobExecutionId.name, jobExecutionId), Job::class.java)

  @Throws(JobBlockedException::class)
  override fun releaseRunLock(jobId: JobId, jobExecutionId: JobExecutionId) {
    val update = Update()
    update.unset(Job::runningJobExecutionId.name)
    if (mongoTemplate.updateFirst<Job>(
        query(where("_id").`is`(jobId).and(Job::runningJobExecutionId.name).`is`(jobExecutionId)), update)
        .modifiedCount != 1L)
      throw JobBlockedException("Tried to release runLock of $jobId but different execution was running")
  }

  override fun findState(jobId: JobId, key: String) = findOne(jobId)?.let {
    it.state[key]
  }

  override fun updateState(jobId: JobId, key: String, value: String?) = findOne(jobId)?.let {
    val state = HashMap(it.state)
    if (value == null) state.remove(key) else state[key] = value
    mongoTemplate.updateFirst<Job>(query(where("_id").`is`(jobId)), update(Job::state.name, state))
    Unit
  }

  override fun findAllJobs(): List<Job> =  mongoTemplate.findAll(Job::class.java)

}
