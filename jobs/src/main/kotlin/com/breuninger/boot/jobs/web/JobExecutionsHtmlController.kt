package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.JobExecution
import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.service.JobExecutionService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
@RequestMapping("/jobexecutions")
class JobExecutionsHtmlController(private val jobExecutionService: JobExecutionService) {

  @GetMapping
  fun getProducts(@RequestParam(value = "jobExecutionId", required = false) jobExecutionId: String?,model: Model): String {
   if(jobExecutionId== null)
    model.addAttribute("jobExecutions", jobExecutionService.findAllJobExecutions())
    else{
     val jobExecutions: MutableList<JobExecution> = ArrayList()
     val jobExecution: JobExecution? = jobExecutionService.findOne(JobExecutionId(jobExecutionId))
     if(jobExecution!= null)
     jobExecutions.add(jobExecution)
     model.addAttribute("jobExecutions", jobExecutions)
   }
    return "jobExecutionOverviewPage"
  }

  @GetMapping("/{jobExecutionId}")
  fun getProducts2(@PathVariable jobExecutionId: String, model: Model): String {
    if(jobExecutionId== null)
      model.addAttribute("jobExecutions", jobExecutionService.findAllJobExecutions())
    else{
      val jobExecutions: MutableList<JobExecution> = ArrayList()
      val jobExecution: JobExecution? = jobExecutionService.findOne(JobExecutionId(jobExecutionId))
      if(jobExecution!= null)
        jobExecutions.add(jobExecution)
      model.addAttribute("jobExecutions", jobExecutions)
    }
    return "jobExecutionOverviewPage"
  }
}
