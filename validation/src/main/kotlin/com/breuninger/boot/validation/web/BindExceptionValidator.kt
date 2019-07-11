package com.breuninger.boot.validation.web

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindException
import org.springframework.validation.Validator
import org.springframework.web.bind.support.WebExchangeBindException

@Component
@ConditionalOnProperty(prefix = "breuni.validation", name = ["enabled"], havingValue = "true")
class BindExceptionValidator(private val webFluxValidator: Validator) {

  fun validate(target: Any): BeanPropertyBindingResult {
    val bindingResult = BeanPropertyBindingResult(target, target.javaClass.simpleName.toLowerCase())
    webFluxValidator.validate(target, bindingResult)
    return bindingResult
  }

  @Throws(BindException::class)
  fun validateAndThrowException(target: Any) {
    val bindingResult = validate(target)
    if (bindingResult.hasErrors()) {
      val method = BindExceptionValidator::class.java.getDeclaredMethod("validateAndThrowException", Any::class.java)
      throw WebExchangeBindException(MethodParameter.forExecutable(method, 0), bindingResult)
    }
  }
}
