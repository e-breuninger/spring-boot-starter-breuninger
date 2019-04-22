package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.service.JobExecutionService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*
import kotlin.collections.ArrayList


@Controller
@RequestMapping("/jobexecutions")
class JobExecutionsHtmlController(private val jobExecutionService: JobExecutionService) {

  @GetMapping
  fun getAllJobExecutions(model: Model): String {
    model.addAttribute("jobExecutions", jobExecutionService.findAllJobExecutions())

    return "jobExecutionOverviewPage"
  }

  @GetMapping("/single/{jobExecutionId}")
  fun getJobExecutionForExecutionId(@PathVariable jobExecutionId: String, model: Model): String {
    if (jobExecutionId == null)
      model.addAttribute("jobExecutions", Collections.EMPTY_LIST)
    else {
      val jobExecutions: MutableList<JobExecution> = ArrayList()
      val jobExecution: JobExecution? = jobExecutionService.findOne(JobExecutionId(jobExecutionId))
      if (jobExecution != null)
        jobExecutions.add(jobExecution)
      model.addAttribute("jobExecutions", jobExecutions)
    }
    return "jobExecutionOverviewPage"
  }

  @GetMapping("/multi/{jobId}")
  fun getJobExecutionsforJobId(@PathVariable jobId: String, model: Model): String {
    if (jobId == null)
      model.addAttribute("jobExecutions", Collections.EMPTY_LIST)
    else {
      val jobExecutions: List<JobExecution> = jobExecutionService.findAllJobExecutions().filter { it.jobId.value == jobId }
      model.addAttribute("jobExecutions", jobExecutions)
    }
    return "jobExecutionOverviewPage"
  }
}
