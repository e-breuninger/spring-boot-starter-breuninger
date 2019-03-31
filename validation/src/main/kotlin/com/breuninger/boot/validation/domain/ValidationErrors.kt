package com.breuninger.boot.validation.domain

data class ValidationErrors(
  val path: String,
  val status: Int,
  val message: String?,
  val errors: Map<String, List<ValidationError>>
)
