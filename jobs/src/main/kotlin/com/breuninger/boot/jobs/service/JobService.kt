package com.breuninger.boot.jobs.service

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

// TODO(BS): sort methods
@Service
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["enabled"], havingValue = "true")
class JobService(private val jobRepository: JobRepository) {

  companion object {

    val LOG: Logger = LoggerFactory.getLogger(JobService::class.java)
  }

  fun create(job: Job) = jobRepository.insert(job)

  fun findState(jobId: JobId, key: String) = jobRepository.findState(jobId, key)

  fun updateState(jobId: JobId, key: String, value: String?) = jobRepository.updateState(jobId, key, value)

  // TODO(BS): filter with currently existing jobs in definition or just get by all jobs that have definitions...
  fun findAll() = jobRepository.findAll()

  fun findOne(jobId: JobId) = jobRepository.findOne(jobId)

  fun update(jobId: JobId, job: Job) = jobRepository.update(jobId, job)
}
