package com.breuninger.boot.togglz.repository

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.togglz.core.Feature
import org.togglz.core.repository.FeatureState

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MongoStateRepositoryIntegrationTest(@Autowired private val mongoStateRepository: MongoStateRepository) {

  @Test
  fun `ensure that saving and finding a feature works`() {
    val feature = Feature { "a" }

    assertThat(mongoStateRepository.getFeatureState(feature)).isNull()
    mongoStateRepository.setFeatureState(FeatureState(feature))

    assertThat(mongoStateRepository.getFeatureState(feature)).isNotNull()
  }
}
