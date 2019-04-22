package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.JobDefinition
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/jobs")
class JobHTMLController(
  private val jobService: JobService,
  private val jobRunnables: List<JobRunnable>?
) {

  @GetMapping
  fun getAllJobs(model: Model): String {
    model.addAttribute("jobs", jobService.findAllJobs())
    var jobDefintions = HashMap<JobId, JobDefinition>();
    jobRunnables?.map { jobRunnable ->  jobDefintions.set(jobRunnable.definition().jobId, jobRunnable.definition())}
    model.addAttribute("jobDefs", jobDefintions)
    return "jobOverviewPage"
  }
}
