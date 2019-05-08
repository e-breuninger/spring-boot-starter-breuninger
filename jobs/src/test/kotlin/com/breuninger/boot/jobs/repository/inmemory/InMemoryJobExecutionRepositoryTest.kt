package com.breuninger.boot.jobs.repository.inmemory

import com.breuninger.boot.jobs.repository.AbstractJobExecutionRepositoryTest

internal class InMemoryJobExecutionRepositoryTest : AbstractJobExecutionRepositoryTest() {

  private val inMemoryJobExecutionRepository = InMemoryJobExecutionRepository()

  override fun getRepository() = inMemoryJobExecutionRepository
}
