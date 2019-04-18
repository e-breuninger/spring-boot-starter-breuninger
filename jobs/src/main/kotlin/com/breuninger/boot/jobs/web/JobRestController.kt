package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/job", method = [RequestMethod.POST])
class JobRestController(
  private val jobService: JobService
) {

  @PostMapping("/start")
  fun getEventCount(@RequestParam(value = "jobId") jobIdValue: String): Job? {
    val jobId = JobId(jobIdValue)
    jobService.startJob(jobId)
    //we just need a little bit of time until the job is started in a new thread and the id is saved so we can get it. this is ugly i know
    Thread.sleep(200)
    return jobService.findOne(jobId)
  }

  @PostMapping ("/disable")
  fun getEventCount(@RequestParam(value = "jobId") jobId: String, @RequestParam(value = "disabled") disabled: Boolean, @RequestBody(required = false) body: String?):Job? {
    jobService.disableJob(disabled,if(body != null) body else "",JobId(jobId))
    return  jobService.findOne(JobId(jobId))
  }
}
