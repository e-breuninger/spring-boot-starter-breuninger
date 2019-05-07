package com.breuninger.boot.jobs.service

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.repository.JobRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(prefix = "breuni.jobs", name = ["enabled"], havingValue = "true")
class JobService(private val jobRepository: JobRepository) {

  fun findOne(jobId: JobId) = jobRepository.findOne(jobId)

  fun findAll() = jobRepository.findAll()

  fun create(job: Job) = jobRepository.create(job)

  fun update(jobId: JobId, job: Job) = jobRepository.updateDisableState(jobId, job)

  fun findState(jobId: JobId, key: String) = jobRepository.findState(jobId, key)

  fun updateState(jobId: JobId, key: String, value: String?) = jobRepository.updateState(jobId, key, value)
}
