package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/jobs")
class JobRestController(private val jobService: JobService) {

  @PutMapping("/{jobId}")
  fun update(@PathVariable(value = "jobId") jobIdValue: String, @RequestBody @Valid job: Job) =
    jobService.update(JobId(jobIdValue), job)
}
