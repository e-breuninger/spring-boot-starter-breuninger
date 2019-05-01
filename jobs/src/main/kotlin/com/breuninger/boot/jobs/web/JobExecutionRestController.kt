package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.service.JobExecutionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/jobExecutions")
class JobExecutionRestController(private val jobExecutionService: JobExecutionService) {

  @GetMapping("/{jobExecutionId}")
  fun getJobExecution(@PathVariable jobExecutionId: String) =
    jobExecutionService.findOne(JobExecutionId(jobExecutionId))
}
