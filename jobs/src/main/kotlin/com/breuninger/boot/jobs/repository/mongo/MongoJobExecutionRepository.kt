package com.breuninger.boot.jobs.repository.mongo

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecution.Status
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update.update
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["mongo.enabled"], havingValue = "true")
class MongoJobExecutionRepository(private val mongoTemplate: MongoTemplate) : JobExecutionRepository {

  override fun findOne(jobExecutionId: JobExecutionId) = mongoTemplate.findById<JobExecution>(jobExecutionId)

  override fun find100DescendingByLastUpdated(jobId: JobId?): List<JobExecution> {
    val query = Query()
    jobId?.let { query.addCriteria(where(JobExecution::jobId.name).`is`(jobId)) }
    query.with(Sort.by(DESC, JobExecution::lastUpdated.name)).limit(100)
    return mongoTemplate.find(query)
  }

  override fun findAllIgnoreMessages(): List<JobExecution> {
    val query = Query()
    query.fields().slice(JobExecution::messages.name, 0)
    return mongoTemplate.find(query)
  }

  override fun save(jobExecution: JobExecution) = mongoTemplate.save(jobExecution)

  override fun updateStatus(jobExecutionId: JobExecutionId, status: Status) {
    mongoTemplate.updateFirst<JobExecution>(query(where("_id").`is`(jobExecutionId)),
      update(JobExecution::status.name, status))
  }

  override fun updateLastUpdated(jobExecutionId: JobExecutionId, lastUpdated: Instant) {
    mongoTemplate.updateFirst<JobExecution>(query(where("_id").`is`(jobExecutionId)),
      update(JobExecution::lastUpdated.name, lastUpdated))
  }

  override fun appendMessage(jobExecutionId: JobExecutionId, message: JobExecutionMessage) {
    mongoTemplate.updateFirst<JobExecution>(query(where("_id").`is`(jobExecutionId)),
      update(JobExecution::lastUpdated.name, message.timestamp)
        .addToSet(JobExecution::messages.name, message))
  }

  override fun stop(jobExecutionId: JobExecutionId, stopped: Instant) {
    mongoTemplate.updateFirst<JobExecution>(query(where("_id").`is`(jobExecutionId)),
      update(JobExecution::stopped.name, stopped)
        .set(JobExecution::lastUpdated.name, stopped))
  }

  override fun remove(jobExecution: JobExecution) {
    mongoTemplate.remove(jobExecution)
  }

  override fun clear() = mongoTemplate.dropCollection<JobExecution>()
}
