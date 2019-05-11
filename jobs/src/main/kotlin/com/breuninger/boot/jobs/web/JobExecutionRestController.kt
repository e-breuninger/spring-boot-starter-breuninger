package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobExecutionService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/jobExecutions")
class JobExecutionRestController(private val jobExecutionService: JobExecutionService) {

  @GetMapping("/{jobExecutionId}", produces = [APPLICATION_JSON_VALUE])
  fun find(@PathVariable jobExecutionId: String) = jobExecutionService.findOne(JobExecutionId(jobExecutionId))

  // TODO(BS): der client erwartet hier einen job zurück zu bekommen. nach dem umbau ist dies nicht mehr der fall
  @PostMapping
  fun create(@RequestParam(value = "jobId") jobIdValue: String) = jobExecutionService.create(JobId(jobIdValue))
}
