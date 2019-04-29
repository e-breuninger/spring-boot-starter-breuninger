package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.service.JobExecutionService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/messages", method = [RequestMethod.GET])
class JobExecutionRestController(private val jobExecutionService: JobExecutionService) {

  @GetMapping
  fun getCurrentExecutionState(@RequestParam(value = "jobExecutionId") jobExecutionId: String): JobExecution? {

    return jobExecutionService.findOne(JobExecutionId(jobExecutionId))
  }
}