package com.breuninger.boot.jobs.repository.mongo

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecution.Status
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobExecutionMessage
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update.update
import org.springframework.data.mongodb.core.updateFirst
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.Instant.now

// TODO(BS): sort methods
@Repository
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["mongo.enabled"], havingValue = "true")
class MongoJobExecutionRepository(private val mongoTemplate: MongoTemplate) : JobExecutionRepository {

  override fun findAll(jobExecutionId: JobExecutionId): List<JobExecution> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun findOne(jobExecutionId: JobExecutionId) = mongoTemplate.findById<JobExecution>(jobExecutionId)

  override fun findAllWithoutMessages(): List<JobExecution> {
    val query = Query()
    query.fields().slice(JobExecution::messages.name, 0)
    return mongoTemplate.find(query)
  }

  // TODO(KA): better name and tests
  // TODO(BS): can do that better with mongo query and not sorting afterwards
  // TODO(BS): need to add jobId filter if not null
  // TODO(BS): take 100
  override fun findAll(jobId: JobId?): List<JobExecution> = mongoTemplate.findAll(JobExecution::class.java)
    .sortedBy { it.lastUpdated }
    .reversed()

  override fun save(jobExecution: JobExecution) = mongoTemplate.save(jobExecution)

  override fun remove(jobExecution: JobExecution) {
    mongoTemplate.remove(jobExecution)
  }

  override fun stop(jobExecutionId: JobExecutionId) {
    val stopped = now()
    mongoTemplate.updateFirst<JobExecution>(
      query(where("_id").`is`(jobExecutionId)),
      update(JobExecution::stopped.name, stopped).set(JobExecution::lastUpdated.name, stopped))
  }

  override fun updateStatus(jobExecutionId: JobExecutionId, status: Status) {
    mongoTemplate.updateFirst<JobExecution>(
      query(where("_id").`is`(jobExecutionId)),
      update(JobExecution::status.name, status))
  }

  override fun appendMessage(jobExecutionId: JobExecutionId, message: JobExecutionMessage) {
    mongoTemplate.updateFirst<JobExecution>(
      query(where("_id").`is`(jobExecutionId)),
      update(JobExecution::lastUpdated.name, message.timestamp).addToSet(JobExecution::messages.name, message))
  }

  override fun updateLastUpdated(jobExecutionId: JobExecutionId, lastUpdated: Instant) {
    mongoTemplate.updateFirst<JobExecution>(
      query(where("_id").`is`(jobExecutionId)),
      update(JobExecution::lastUpdated.name, lastUpdated))
  }
}
