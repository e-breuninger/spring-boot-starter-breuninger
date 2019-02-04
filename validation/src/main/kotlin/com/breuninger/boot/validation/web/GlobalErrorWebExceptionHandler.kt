package com.breuninger.boot.validation.web

import com.breuninger.boot.validation.autoconfiguration.ValidationProperties
import org.springframework.boot.autoconfigure.web.ErrorProperties
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class GlobalErrorWebExceptionHandler(
  private val validationProperties: ValidationProperties,
  errorAttributes: ErrorAttributes,
  resourceProperties: ResourceProperties,
  errorProperties: ErrorProperties,
  applicationContext: ApplicationContext
) : DefaultErrorWebExceptionHandler(errorAttributes, resourceProperties, errorProperties, applicationContext) {

  override fun renderErrorResponse(request: ServerRequest): Mono<ServerResponse> {
    val errorAttributes = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL))
    val httpStatus = getHttpStatus(errorAttributes)
    return ServerResponse.status(httpStatus)
      .contentType(MediaType.parseMediaType(validationProperties.errorMediaType))
      .syncBody(getWithoutStatus(errorAttributes))
      .doOnNext { logError(request, httpStatus) }
  }

  private fun getWithoutStatus(errorAttributes: Map<String, Any>): Map<String, Any> {
    val withoutStatus = LinkedHashMap(errorAttributes)
    withoutStatus.remove("status")
    return withoutStatus
  }
}
