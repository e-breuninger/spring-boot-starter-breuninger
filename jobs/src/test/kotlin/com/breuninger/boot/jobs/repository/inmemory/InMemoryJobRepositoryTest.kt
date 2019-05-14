package com.breuninger.boot.jobs.repository.inmemory

import com.breuninger.boot.jobs.repository.AbstractJobRepositoryTest

class InMemoryJobRepositoryTest : AbstractJobRepositoryTest() {

  private val inMemoryJobRepository = InMemoryJobRepository()

  override fun getRepository() = inMemoryJobRepository
}
