package com.breuninger.boot.validation.constraintvalidators

import com.breuninger.boot.validation.constraints.IsEnum
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class EnumValidator : ConstraintValidator<IsEnum, String> {

  private var availableEnumNames: Set<String> = HashSet()
  private var ignoreCase = false
  private var allowNull = false

  override fun initialize(annotation: IsEnum) {
    availableEnumNames = annotation.enumClass.java.enumConstants.map { it.name }.toSet()
    ignoreCase = annotation.ignoreCase
    allowNull = annotation.allowNull
  }

  override fun isValid(value: String?, context: ConstraintValidatorContext) = value?.let {
    availableEnumNames.any {
      return if (ignoreCase) {
        it.equals(value, ignoreCase = true)
      } else {
        it == value
      }
    }
  } ?: allowNull
}
