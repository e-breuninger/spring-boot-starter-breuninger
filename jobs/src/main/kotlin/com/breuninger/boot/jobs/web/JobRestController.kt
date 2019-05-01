package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/job")
class JobRestController(private val jobService: JobService) {

  @PostMapping("/{jobId}/start")
  fun startJob(@PathVariable(value = "jobId") jobIdValue: String): Job? {
    val jobId = JobId(jobIdValue)
    jobService.startJob(jobId)
    // TODO(BS): I think we can change this
    Thread.sleep(200)
    return jobService.findOne(jobId)
  }

  // TODO(BS): this interface looks strange - maybe put message and disabled into the body as a json
  @PostMapping("/{jobId}/disable")
  fun disableEnableJob(@PathVariable(value = "jobId") jobIdValue: String,
                       @RequestParam disabled: Boolean,
                       @RequestBody(required = false) body: String?): Job? {
    val jobId = JobId(jobIdValue)
    jobService.disableJob(disabled, body ?: "", jobId)
    return jobService.findOne(jobId)
  }
}
