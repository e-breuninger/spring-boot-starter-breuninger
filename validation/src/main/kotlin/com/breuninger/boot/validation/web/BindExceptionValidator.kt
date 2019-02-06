package com.breuninger.boot.validation.web

import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindException
import org.springframework.validation.Validator

@Component
class BindExceptionValidator(private val webFluxValidator: Validator) {

  fun validate(target: Any): BeanPropertyBindingResult {
    val bindingResult = BeanPropertyBindingResult(target, target.javaClass.simpleName.toLowerCase())
    webFluxValidator.validate(target, bindingResult)
    return bindingResult
  }

  fun validateAndThrowException(target: Any) {
    val bindingResult = validate(target)
    if (bindingResult.hasErrors()) {
      throw BindException(bindingResult)
    }
  }
}
