package com.breuninger.boot.jobs.repository.mongo

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.repository.AbstractJobExecutionRepositoryTest
import com.breuninger.boot.jobs.repository.JobExecutionRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.dropCollection

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class MongoJobExecutionRepositoryTest(@Autowired private val mongoJobExecutionRepository: MongoJobExecutionRepository, @Autowired private val mongoTemplate: MongoTemplate): AbstractJobExecutionRepositoryTest() {

  override fun getRepository(): JobExecutionRepository {
    return mongoJobExecutionRepository
  }

  @BeforeEach
  fun before(){
    mongoTemplate.dropCollection<JobExecution>()
  }
}