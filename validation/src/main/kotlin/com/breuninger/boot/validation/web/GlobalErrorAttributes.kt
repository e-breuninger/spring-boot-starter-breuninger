package com.breuninger.boot.validation.web

import com.breuninger.boot.validation.domain.ValidationError
import com.breuninger.boot.validation.domain.ValidationErrorsHalRepresentation
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ResponseStatusException

class GlobalErrorAttributes(private val objectMapper: ObjectMapper) : DefaultErrorAttributes() {

  override fun getErrorAttributes(request: ServerRequest, includeStackTrace: Boolean): Map<String, Any> {
    val error = getError(request)
    val errorStatus = determineHttpStatus(error)
    val validationErrorsHalRepresentation = ValidationErrorsHalRepresentation(
      request.path(),
      errorStatus.value(),
      determineMessage(error),
      handleException(determineException(error))
    )
    return objectMapper.convertValue(validationErrorsHalRepresentation, object : TypeReference<Map<String, Any>>() {})
  }

  private fun handleException(error: Throwable): Map<String, List<ValidationError>> {
    val fieldValidationErrors = HashMap<String, MutableList<ValidationError>>()
    if (error is BindingResult && error.hasErrors()) {
      error.allErrors.forEach {
        if (it is FieldError)
          handleFieldError(it, fieldValidationErrors)
        else
          handleObjectError(it, fieldValidationErrors)
      }
    }
    return fieldValidationErrors
  }

  private fun handleFieldError(fieldError: FieldError, fieldValidationErrors: HashMap<String, MutableList<ValidationError>>) {
    val key = fieldError.objectName + "." + fieldError.field
    val validationErrors = fieldValidationErrors.getOrDefault(key, ArrayList())
    validationErrors.add(ValidationError(fieldError.defaultMessage, fieldError.rejectedValue))
    fieldValidationErrors[key] = validationErrors
  }

  private fun handleObjectError(objectError: ObjectError, fieldValidationErrors: HashMap<String, MutableList<ValidationError>>) {
    val key = objectError.objectName
    val validationErrors = fieldValidationErrors.getOrDefault(key, ArrayList())
    validationErrors.add(ValidationError(objectError.defaultMessage, null))
    fieldValidationErrors[key] = validationErrors
  }

  private fun determineHttpStatus(error: Throwable): HttpStatus {
    if (error is ResponseStatusException) {
      return error.status
    }
    val responseStatus = AnnotatedElementUtils.findMergedAnnotation(error.javaClass, ResponseStatus::class.java)
    return responseStatus?.code ?: HttpStatus.INTERNAL_SERVER_ERROR
  }

  private fun determineMessage(error: Throwable): String? {
    when (error) {
      is WebExchangeBindException -> return error.reason
      is ResponseStatusException -> return error.reason
    }
    val responseStatus = AnnotatedElementUtils.findMergedAnnotation(error.javaClass, ResponseStatus::class.java)
    return responseStatus?.reason ?: error.message
  }

  private fun determineException(error: Throwable): Throwable {
    return if (error is ResponseStatusException) {
      error.cause ?: error
    } else error
  }
}
