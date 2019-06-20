package com.breuninger.boot.core.util

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.breuninger.boot.core.util.SlugificationUtil.slugify
import org.junit.jupiter.api.Test

class SlugificationUtilTest {

  @Test
  fun expectToSlugifyStringWithUmlauts() {
    assertThat(slugify("Schöne neue Röhrenjeans in Größe 42")).isEqualTo("schoene-neue-roehrenjeans-in-groesse-42")
  }

  @Test
  fun expectToSlugifyStringWithForeignChars() {
    assertThat(slugify("Haup(t)hose_+*~#'/-\"'un[d]so--Wahns{i}n.n;")).isEqualTo("haup-t-hose_-un-d-so-wahns-i-n-n")
  }

  @Test
  fun expectToReturnEmptyStringForEmptyInput() {
    assertThat(slugify("")).isEmpty()
  }
}
