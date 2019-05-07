package com.breuninger.boot.jobs.repository.inmemory

import com.breuninger.boot.jobs.repository.AbstractJobRepositoryTest
import com.breuninger.boot.jobs.repository.JobRepository

// TODO potentially implement more tests see TODOs in repository classes
internal class InMemoryJobRepositoryTest : AbstractJobRepositoryTest() {

  private val inMemoryJobRepository = InMemoryJobRepository()

  override fun getRepository(): JobRepository {
    return inMemoryJobRepository
  }
}
