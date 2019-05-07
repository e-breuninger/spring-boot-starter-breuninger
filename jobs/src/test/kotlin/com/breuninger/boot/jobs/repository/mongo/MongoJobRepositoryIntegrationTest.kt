package com.breuninger.boot.jobs.repository.mongo

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.repository.AbstractJobRepositoryTest
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.dropCollection

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class MongoJobRepositoryIntegrationTest(
  private val mongoJobRepository: MongoJobRepository,
  private val mongoTemplate: MongoTemplate
) : AbstractJobRepositoryTest() {

  override fun getRepository() = mongoJobRepository

  // TODO(KA): add as method to repository
  @BeforeEach
  fun before() = mongoTemplate.dropCollection<Job>()
}
