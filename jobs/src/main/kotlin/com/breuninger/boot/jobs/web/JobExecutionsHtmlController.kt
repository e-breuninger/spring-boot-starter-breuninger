package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.domain.JobExecutionId
import com.breuninger.boot.jobs.service.JobExecutionService
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam


@Controller
@RequestMapping("/jobexecutions")
class JobExecutionsOverviewController(private val jobExecutionService: JobExecutionService) {

  @GetMapping
  fun getProducts(model: Model): String {
    model.addAttribute("jobExecutions", jobExecutionService.findAllJobExecutions())
    return "jobExecutionOverviewPage"
  }

  @RequestMapping("/messages", method = [RequestMethod.GET])
  fun getEventCount(@RequestParam(value = "jobExecutionId") jobExecutionId: String): String {
    map.addAttribute("jobExecution", jobExecutionService.findOne(JobExecutionId(jobExecutionId)))

    return
  }
}
