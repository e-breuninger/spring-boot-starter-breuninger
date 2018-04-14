package com.breuninger.boot.jobs.configuration;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.stream.Collectors.toList;

import static com.breuninger.boot.status.domain.StatusDetail.statusDetail;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;
import com.breuninger.boot.jobs.repository.JobMetaRepository;
import com.breuninger.boot.jobs.repository.JobRepository;
import com.breuninger.boot.jobs.repository.cleanup.DeleteSkippedJobs;
import com.breuninger.boot.jobs.repository.cleanup.KeepLastJobs;
import com.breuninger.boot.jobs.repository.cleanup.StopDeadJobs;
import com.breuninger.boot.jobs.repository.inmem.InMemJobMetaRepository;
import com.breuninger.boot.jobs.repository.inmem.InMemJobRepository;
import com.breuninger.boot.jobs.service.JobDefinitionService;
import com.breuninger.boot.jobs.service.JobService;
import com.breuninger.boot.jobs.status.JobStatusCalculator;
import com.breuninger.boot.jobs.status.JobStatusDetailIndicator;
import com.breuninger.boot.status.domain.Status;
import com.breuninger.boot.status.indicator.CompositeStatusDetailIndicator;
import com.breuninger.boot.status.indicator.StatusDetailIndicator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties({JobsProperties.class, BreuningerApplicationProperties.class})
public class JobsConfiguration {

  private final JobsProperties jobsProperties;
  private final String breuningerManagementBasePath;

  public JobsConfiguration(final JobsProperties jobsProperties, final BreuningerApplicationProperties applicationProperties) {
    this.jobsProperties = jobsProperties;
    breuningerManagementBasePath = applicationProperties.getManagement().getBasePath();
    final var calculator = this.jobsProperties.getStatus().getCalculator();
    if (!calculator.containsKey("default")) {
      this.jobsProperties.getStatus().setCalculator(new HashMap() {{
        putAll(calculator);
        put("default", "warningOnLastJobFailed");
      }});
    }
  }

  @Bean
  @ConditionalOnMissingBean(ScheduledExecutorService.class)
  public ScheduledExecutorService scheduledExecutorService() {
    return newScheduledThreadPool(jobsProperties.getThreadCount(), new ThreadFactory() {
      private final AtomicInteger num = new AtomicInteger();

      @Override
      public Thread newThread(final Runnable r) {
        return new Thread(r, "spring-boot-starter-breuninger-ScheduledExecutorService-" + num.getAndAdd(1));
      }
    });
  }

  @Bean
  @ConditionalOnMissingBean(JobMetaRepository.class)
  public JobMetaRepository jobMetaRepository() {
    return new InMemJobMetaRepository();
  }

  @Bean
  @ConditionalOnMissingBean(JobRepository.class)
  public JobRepository jobRepository() {
    LOG.warn("===============================");
    LOG.warn("Using in-memory JobRepository");
    LOG.warn("===============================");
    return new InMemJobRepository();
  }

  @Bean
  @ConditionalOnMissingBean(KeepLastJobs.class)
  public KeepLastJobs keepLastJobsStrategy(final JobRepository jobRepository) {
    return new KeepLastJobs(jobRepository, jobsProperties.getCleanup().getNumberOfJobsToKeep());
  }

  @Bean
  @ConditionalOnMissingBean(StopDeadJobs.class)
  public StopDeadJobs deadJobStrategy(final JobService jobService) {
    return new StopDeadJobs(jobService, jobsProperties.getCleanup().getMarkDeadAfter());
  }

  @Bean
  @ConditionalOnMissingBean(DeleteSkippedJobs.class)
  public DeleteSkippedJobs deleteSkippedJobsStrategy(final JobRepository jobRepository) {
    return new DeleteSkippedJobs(jobRepository, jobsProperties.getCleanup().getNumberOfSkippedJobsToKeep());
  }

  @Bean
  public JobStatusCalculator warningOnLastJobFailed(final JobRepository jobRepository) {
    return JobStatusCalculator.warningOnLastJobFailed("warningOnLastJobFailed", jobRepository, breuningerManagementBasePath);
  }

  @Bean
  public JobStatusCalculator errorOnLastJobFailed(final JobRepository jobRepository) {
    return JobStatusCalculator.errorOnLastJobFailed("errorOnLastJobFailed", jobRepository, breuningerManagementBasePath);
  }

  @Bean
  public JobStatusCalculator errorOnLastThreeJobsFailed(final JobRepository jobRepository) {
    return JobStatusCalculator.errorOnLastNumJobsFailed("errorOnLastThreeJobsFailed", 3, jobRepository,
      breuningerManagementBasePath);
  }

  @Bean
  public JobStatusCalculator errorOnLastTenJobsFailed(final JobRepository jobRepository) {
    return JobStatusCalculator.errorOnLastNumJobsFailed("errorOnLastTenJobsFailed", 10, jobRepository,
      breuningerManagementBasePath);
  }

  @Bean
  @ConditionalOnProperty(name = "breuninger.jobs.status.enabled", havingValue = "true", matchIfMissing = true)
  public StatusDetailIndicator jobStatusDetailIndicator(final JobDefinitionService service,
                                                        final List<JobStatusCalculator> calculators) {
    final var jobDefinitions = service.getJobDefinitions();

    if (jobDefinitions.isEmpty()) {
      return () -> statusDetail("Jobs", Status.OK, "No job definitions configured in application.");
    } else {
      return new CompositeStatusDetailIndicator("Jobs", jobDefinitions.stream()
        .map(d -> new JobStatusDetailIndicator(d, findJobStatusCalculator(d.jobType(), calculators)))
        .collect(toList()));
    }
  }

  private JobStatusCalculator findJobStatusCalculator(final String jobType, final List<JobStatusCalculator> calculators) {
    final var statusCalculators = jobsProperties.getStatus().getCalculator();
    final String calculator;
    final var normalizedJobType = jobType.toLowerCase().replace(" ", "-");
    if (statusCalculators.containsKey(normalizedJobType)) {
      calculator = statusCalculators.get(normalizedJobType);
    } else {
      calculator = statusCalculators.get("default");
    }
    return calculators.stream()
      .filter(c -> calculator.equalsIgnoreCase(c.getKey()))
      .findAny()
      .orElseThrow(() -> new IllegalStateException("Unable to find JobStatusCalculator " + calculator));
  }
}
