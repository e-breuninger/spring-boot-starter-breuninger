package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.JobRunnable
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/jobstart", method = [RequestMethod.POST])
class JobStartRestController(
  private val jobRunnables: List<JobRunnable>?
) {

  @PostMapping
  fun getEventCount(@RequestParam(value = "jobId") jobId: String) {
    //FIXME does this work, what should be checked here before starting?
    jobRunnables?.map { jobRunnable -> { if (jobRunnable.definition().jobId.value.equals(jobId)) jobRunnable.execute() } }
  }
}
