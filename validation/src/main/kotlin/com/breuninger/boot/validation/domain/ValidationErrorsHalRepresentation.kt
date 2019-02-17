package com.breuninger.boot.validation.domain

import de.otto.edison.hal.HalRepresentation
import de.otto.edison.hal.Link.profile
import de.otto.edison.hal.Links.linkingTo

data class ValidationErrorsHalRepresentation(
  val path: String,
  val status: Int,
  val message: String?,
  val errors: Map<String, List<ValidationError>>
  // TODO add spec profile url
) : HalRepresentation(linkingTo().array(profile("http://specs.breuninger.de/profiles/error")).build())
