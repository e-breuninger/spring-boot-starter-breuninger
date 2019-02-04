package com.breuninger.boot.validation.constraintvalidators

import com.breuninger.boot.validation.constraints.IsInstant
import java.time.Instant
import java.time.format.DateTimeParseException
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class InstantValidator : ConstraintValidator<IsInstant, String> {

  override fun initialize(constraintAnnotation: IsInstant?) {
  }

  override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
    return value?.let {
      return try {
        Instant.parse(value)
        true
      } catch (dateTimeParseException: DateTimeParseException) {
        false
      }
    } ?: true
  }
}
