package com.breuninger.boot.validation.domain

import de.otto.edison.hal.HalRepresentation
import de.otto.edison.hal.Link.profile
import de.otto.edison.hal.Links.linkingTo

data class ValidationErrorsHalRepresentation(
  val path: String,
  val status: Int,
  val message: String?,
  val errors: Map<String, List<ValidationError>>
) : HalRepresentation(linkingTo().array(profile("http://spec.breuninger.com/profiles/error")).build())
