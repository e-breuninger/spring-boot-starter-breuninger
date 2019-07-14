package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobExecutionService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/jobExecutions")
class JobExecutionHtmlController(private val jobExecutionService: JobExecutionService) {

  @GetMapping("/{jobExecutionId}")
  fun find(@PathVariable jobExecutionId: String, model: Model): String {
    model.addAttribute("jobExecutionId", JobExecutionId(jobExecutionId))
    model.addAttribute("jobExecutions", listOfNotNull(jobExecutionService.findOne(JobExecutionId(jobExecutionId))))
    return "jobExecutions"
  }

  @GetMapping
  fun findAll(@RequestParam(required = false) jobId: String?, model: Model): String {
    jobId?.let { model.addAttribute("jobId", JobId(it)) }
    model.addAttribute("jobExecutions", jobExecutionService.find100DescendingByLastUpdated(jobId?.let { JobId(it) }))
    return "jobExecutions"
  }
}
