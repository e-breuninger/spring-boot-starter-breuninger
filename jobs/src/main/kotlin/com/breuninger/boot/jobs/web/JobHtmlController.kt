package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/jobs")
class JobHtmlController(private val jobService: JobService, private val jobRunnables: List<JobRunnable>?) {

  @GetMapping
  fun getJobs(model: Model): String {
    model.addAttribute("jobs", jobService.findAll())
    model.addAttribute("jobDefintions", jobRunnables?.map { it.definition().jobId to it.definition() }?.toMap())
    return "jobs"
  }

  @GetMapping("/{jobId}")
  fun getJob(@PathVariable jobId: String, model: Model): String {
    model.addAttribute("jobs", jobService.findAll(JobId(jobId)))
    model.addAttribute("jobDefintions", jobRunnables?.map { it.definition().jobId to it.definition() }?.toMap())
    return "jobs"
  }
}