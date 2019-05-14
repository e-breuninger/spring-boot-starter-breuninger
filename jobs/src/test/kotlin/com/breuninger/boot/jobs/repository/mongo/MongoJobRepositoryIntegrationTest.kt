package com.breuninger.boot.jobs.repository.mongo

import com.breuninger.boot.jobs.repository.AbstractJobRepositoryTest
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MongoJobRepositoryIntegrationTest(
  private val mongoJobRepository: MongoJobRepository
) : AbstractJobRepositoryTest() {

  override fun getRepository() = mongoJobRepository
}
