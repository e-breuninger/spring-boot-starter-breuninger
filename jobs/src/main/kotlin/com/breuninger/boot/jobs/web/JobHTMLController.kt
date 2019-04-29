package com.breuninger.boot.jobs.web

import com.breuninger.boot.jobs.JobRunnable
import com.breuninger.boot.jobs.domain.Job
import com.breuninger.boot.jobs.domain.JobDefinition
import com.breuninger.boot.jobs.domain.JobId
import com.breuninger.boot.jobs.service.JobExecutionService
import com.breuninger.boot.jobs.service.JobService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/jobs")
class JobHTMLController(
  private val jobService: JobService,
  private val jobExecutionService: JobExecutionService,
  private val jobRunnables: List<JobRunnable>?
) {

  @GetMapping
  fun getAllJobs(model: Model): String {
    model.addAttribute("jobs", jobService.findAllJobs())
    var jobDefintions = HashMap<JobId, JobDefinition>()
    jobRunnables?.map { jobRunnable -> jobDefintions[jobRunnable.definition().jobId] = jobRunnable.definition() }
    model.addAttribute("jobDefs", jobDefintions)
    return "jobOverviewPage"
  }

  @GetMapping("/{jobId}")
  fun getSpecificJob(@PathVariable jobId: String,model: Model): String {
    val job: Job? = jobService.findOne(JobId(jobId))
    val jobs: MutableList<Job> = ArrayList()
    if(job != null)
      jobs.add(job)
    model.addAttribute("jobs", jobs)
    var jobDefintions = HashMap<JobId, JobDefinition>()
    jobRunnables?.map { jobRunnable -> jobDefintions[jobRunnable.definition().jobId] = jobRunnable.definition() }
    model.addAttribute("jobDefs", jobDefintions)
    return "jobOverviewPage"
  }
}
