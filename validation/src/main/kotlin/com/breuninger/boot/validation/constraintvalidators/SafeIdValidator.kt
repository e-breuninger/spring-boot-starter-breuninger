package com.breuninger.boot.validation.constraintvalidators

import com.breuninger.boot.validation.constraints.SafeId
import java.util.regex.Pattern
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class SafeIdValidator : ConstraintValidator<SafeId, String> {

  companion object {

    private val SAFE_ID_PATTERN = Pattern.compile("[a-zA-Z0-9\\-_]*")
  }

  override fun initialize(safeId: SafeId?) {
  }

  override fun isValid(id: String?, context: ConstraintValidatorContext) =
    id?.let { SAFE_ID_PATTERN.matcher(it).matches() } ?: true
}
