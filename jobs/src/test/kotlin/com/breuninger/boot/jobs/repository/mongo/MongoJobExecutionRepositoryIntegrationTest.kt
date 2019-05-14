package com.breuninger.boot.jobs.repository.mongo

import com.breuninger.boot.jobs.repository.AbstractJobExecutionRepositoryTest
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MongoJobExecutionRepositoryIntegrationTest(
  private val mongoJobExecutionRepository: MongoJobExecutionRepository
) : AbstractJobExecutionRepositoryTest() {

  override fun getRepository() = mongoJobExecutionRepository
}
