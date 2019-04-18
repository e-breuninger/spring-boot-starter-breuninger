package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/jobstart", method = [RequestMethod.POST])
class JobStartRestController(
  private val jobService: JobService
) {

  @PostMapping
  fun getEventCount(@RequestParam(value = "jobId") jobIdValue: String): Job? {
    val jobId = JobId(jobIdValue)
    jobService.startJob(jobId)
    //we just need a little bit of time until the job is started in a new thread and the id is saved so we can get it. this is ugly i know
    Thread.sleep(200)
    return jobService.findOne(jobId)
  }
}
