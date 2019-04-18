package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/jobsdisable", method = [RequestMethod.POST])
class JobDisableRestController(
  private val jobService: JobService
) {

  @PostMapping
  fun getEventCount(@RequestParam(value = "jobId") jobId: String, @RequestParam(value = "disabled") disabled: Boolean, @RequestBody(required = false) body: String?):Job? {
    jobService.disableJob(disabled,if(body != null) body else "",JobId(jobId))
    return  jobService.findOne(JobId(jobId))
  }
}
