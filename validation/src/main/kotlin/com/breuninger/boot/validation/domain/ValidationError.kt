package com.breuninger.boot.validation.domain

data class ValidationError(
  val message: String?,
  val rejectedValue: Any?
)
