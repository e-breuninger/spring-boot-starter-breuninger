package com.breuninger.boot.jobs.domain

import org.slf4j.Marker
import org.slf4j.MarkerFactory

object JobMarker {

  private const val JOB = "JOB"

  val JOB_MARKER: Marker = MarkerFactory.getMarker(JOB)

  fun jobMarkerFor(jobId: JobId): Marker {
    val marker = MarkerFactory.getMarker(jobId.value)
    JOB_MARKER.add(marker)
    return marker
  }
}
