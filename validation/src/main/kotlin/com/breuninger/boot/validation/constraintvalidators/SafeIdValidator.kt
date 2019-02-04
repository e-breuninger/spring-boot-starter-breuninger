package com.breuninger.boot.validation.constraintvalidators

import com.breuninger.boot.validation.constraints.SafeId
import java.util.regex.Pattern
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class SafeIdValidator : ConstraintValidator<SafeId, String> {

  override fun initialize(safeId: SafeId?) {
  }

  override fun isValid(id: String?, context: ConstraintValidatorContext): Boolean {
    return id?.let { safeIdPattern.matcher(id).matches() } ?: true
  }

  companion object {
    private val safeIdPattern = Pattern.compile("[a-zA-Z0-9\\-_]*")
  }
}
