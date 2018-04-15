# spring-boot-starter-breuninger-jobs

Simple addon library to support background jobs for spring-boot-starter-breuninger.

## About

This library offers the possibility to execute background jobs like, for example, importing data from other services
on a regular basis.

Job information can automatically be persisted in a MongoDB if you use spring-boot-starter-breuninger-mongo. Otherwise they are only persisted 
in memory. In clustered environments (multiple instances of a single service) it is generally a good idea to use some
kind of persistence. 

It is possible to persist job information in different data stores. In this case, a `JobRepository` must be implemented 
and exposed as a Spring Bean.

Beside of starting a job programmatically via the `Jobservice` you can also use the graphical user interface which 
comes with this library. Links to this UI are automatically added to the service´s /internal pages menu bar if
spring-boot-starter-breuninger-jobs is added to the classpath.

The scheduling of the jobs is not part of this framework. Internal triggers are easy to implement
using Spring's @EnableScheduling and @Scheduled annotations.

Or implementing the `SchedulingConfigurer` like this:
```java
@Component
public final class JobWatcher implements SchedulingConfigurer {

  private final JobDefinitionService jobDefinitionService;
  private final JobService jobService;

  @Override
  public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
    jobDefinitionService.getJobDefinitions().forEach(def -> {
      def.cron().ifPresent(cron -> registerCronJob(def.jobType(), cron, taskRegistrar));
      def.fixedDelay().ifPresent(fixedDelay -> registerFixedDelay(def.jobType(), fixedDelay, taskRegistrar));
    });
  }

  private void registerCronJob(final String jobType, final String cron, final ScheduledTaskRegistrar taskRegistrar) {
    LOG.info("register job {} for cron scheduling: {}", jobType, cron);
    taskRegistrar.addCronTask(() -> jobService.startAsyncJob(jobType), cron);
  }

  private void registerFixedDelay(final String jobType, final Duration fixedDelay, final ScheduledTaskRegistrar taskRegistrar) {
    LOG.info("register job {} for scheduling with fixed delay: {}", jobType, fixedDelay);
    taskRegistrar.addFixedDelayTask(
      new IntervalTask(() -> jobService.startAsyncJob(jobType), fixedDelay.toMillis(), fixedDelay.toMillis()));
  }
}
```

For the usage of spring-boot-starter-breuninger-jobs take a look at example-jobs.

## Usage

*PENDING*

### JobMutexHandler

You can define JobMutex-Groups to define, that certain jobs may not be executed, while other specific jobs are running.

To define a mutex group you need to define a bean of type JobMutexGroup
