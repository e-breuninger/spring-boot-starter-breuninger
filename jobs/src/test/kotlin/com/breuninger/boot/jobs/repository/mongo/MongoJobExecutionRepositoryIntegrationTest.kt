package com.breuninger.boot.jobs.repository.mongo

import com.breuninger.boot.jobs.repository.AbstractJobExecutionRepositoryTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class MongoJobExecutionRepositoryIntegrationTest(
  @Autowired private val mongoJobExecutionRepository: MongoJobExecutionRepository
) : AbstractJobExecutionRepositoryTest() {

  override fun getRepository() = mongoJobExecutionRepository
}
