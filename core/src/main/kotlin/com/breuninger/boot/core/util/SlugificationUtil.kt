package com.breuninger.boot.core.util

import com.github.slugify.Slugify

object SlugificationUtil {

  private val slugify = Slugify()

  fun slugify(text: String): String {
    return slugify.slugify(text)
  }
}
