package com.breuninger.boot.jobs.repository.inmemory

import com.breuninger.boot.jobs.repository.AbstractJobExecutionRepositoryTest
import com.breuninger.boot.jobs.repository.JobExecutionRepository

internal class InMemoryJobExecutionRepositoryTest: AbstractJobExecutionRepositoryTest() {

  val inMemoryJobExecutionRepository = InMemoryJobExecutionRepository()

  override fun getRepository(): JobExecutionRepository {
    return inMemoryJobExecutionRepository
  }
}
