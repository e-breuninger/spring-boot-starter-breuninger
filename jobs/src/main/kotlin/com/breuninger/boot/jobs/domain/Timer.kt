package com.breuninger.boot.jobs.domain

data class Timer(
  val name: String = "",
  val description: String = "",
  val extraTags: Array<String> = arrayOf(),
  val histogram: Boolean = false,
  val percentiles: DoubleArray = doubleArrayOf(),
  val longTask: Boolean = false
)
