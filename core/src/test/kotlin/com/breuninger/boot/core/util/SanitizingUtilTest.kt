package com.breuninger.boot.core.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.breuninger.boot.core.util.SanitizingUtil.sanitize
import org.junit.jupiter.api.Test

class SanitizingUtilTest {

  @Test
  fun expectToTrimText() {
    assertThat(sanitize("    i will be remaining   ")).isEqualTo("i will be remaining")
  }

  @Test
  fun expectToTrimTextWithTagsCompletly() {
    assertThat(sanitize("<div> </div>")).isEqualTo("")
  }

  @Test
  fun expectToSanitizeTextWithBTag() {
    assertThat(sanitize("<b>i will remain</b>")).isEqualTo("i will remain")
  }

  @Test
  fun expectToSanitizeTextWithScriptTag() {
    assertThat(sanitize("  <script>i will be completely removed</script>  ")).isEqualTo("")
  }

  @Test
  fun expectToSanitizeTextWithHtmlEntities() {
    assertThat(sanitize("&#x3C;script&#x3E;alert(1)&#x3C;/script&#x3E;")).isEqualTo("")
  }
}
