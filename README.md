# spring-boot-starter-breuninger

Collection of independent libraries on top of Spring Boot to provide a faster setup of jvm microservices.

> "I never did anything by accident, nor did any of my inventions come by accident; they came by work." - Thomas Edison


## Status

[![Next Selected Stories](https://badge.waffle.io/e-breuninger/spring-boot-starter-breuninger.svg?label=Ready&title=Selected)](http://waffle.io/e-breuninger/spring-boot-starter-breuninger)
[![Active Stories](https://badge.waffle.io/e-breuninger/spring-boot-starter-breuninger.svg?label=In%20Progress&title=Doing)](http://waffle.io/e-breuninger/spring-boot-starter-breuninger)

[![build](https://travis-ci.org/e-breuninger/spring-boot-starter-breuninger.svg)](https://travis-ci.org/e-breuninger/spring-boot-starter-breuninger) 
[![codecov](https://codecov.io/gh/e-breuninger/spring-boot-starter-breuninger/branch/master/graph/badge.svg)](https://codecov.io/gh/e-breuninger/spring-boot-starter-breuninger)
[![dependencies](https://www.versioneye.com/user/projects/58b16b4a7b9e15004a98c400/badge.svg?style=flat)](https://www.versioneye.com/user/projects/58b16b4a7b9e15004a98c400)
[![release](https://maven-badges.herokuapp.com/maven-central/com.breuninger.boot/spring-boot-starter-breuninger-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.breuninger.boot/spring-boot-starter-breuninger-core)
[![license](https://img.shields.io/github/license/e-breuninger/spring-boot-starter-breuninger.svg)](./LICENSE)

Have a look at the [release notes](CHANGELOG.md) for details about updates and changes.


## About

This project contains a number of independent libraries on top of Spring Boot to provide a faster setup of jvm microservices.
The libraries are used in different projects at Breuninger.
It's purpose is to provide a common implementation for cross-cutting requirements like:

* Health checks that are used to tell the load balancer or mesos platform whether or not a service is healthy.
* A [status page/document](https://github.com/e-breuninger/spring-boot-starter-breuninger/tree/master/spring-boot-starter-breuninger-core) that is used to give information about the current state of the service. Status information also include details about sub-components, background jobs like imports, and so on.
* A simple job handling library that is used to run asynchronous background jobs, which for example can be used to run data imports from other systems.
* An optional MongoDB-based implementation of a JobRepository
* Support for MongoDB-based repositories in case you do not like Spring Data
* Reporting of metrics to Graphite
* Support for feature toggles based on [Togglz](https://www.togglz.org/)

... plus all the features of [Spring Boot](http://projects.spring.io/spring-boot/).


## Future Releases aka Roadmap

[Semantic Versioning v2.0.0](http://semver.org/spec/v2.0.0.html) is used to specify the version numbers.

This project maintains its roadmap with [issues](https://github.com/e-breuninger/spring-boot-starter-breuninger/issues) and [milestones](https://github.com/e-breuninger/spring-boot-starter-breuninger/milestones).

**[1.0.0](https://github.com/e-breuninger/spring-boot-starter-breuninger/milestone/1)**: spring-boot-starter-breuninger for Spring Boot 1.4 &#10004;

**[1.x.0](https://github.com/e-breuninger/spring-boot-starter-breuninger/milestone/2)**: spring-boot-starter-breuninger for Spring Boot 1.5 &#10004;

**[2.0.0](https://github.com/e-breuninger/spring-boot-starter-breuninger/milestone/3)**: spring-boot-starter-breuninger for Spring Boot 2.0


## Documentation

Edison Modules:
* [`spring-boot-starter-breuninger-core`](spring-boot-starter-breuninger-core/README.md): Main library of spring-boot-starter-breuninger.
* [`spring-boot-starter-breuninger-jobs`](spring-boot-starter-breuninger-jobs/README.md): Optional module providing a simple job library.
* [`spring-boot-starter-breuninger-mongo`](spring-boot-starter-breuninger-mongo/README.md): Auto-configuration for MongoDB repositories plus implementation of MongoJobRepository and
 Togglz StateRepository.
* [`spring-boot-starter-breuninger-togglz`](spring-boot-starter-breuninger-togglz/README.md): Optional support for feature toggles for spring-boot-starter-breuninger based on [Togglz](https://www.togglz.org/).
* `spring-boot-starter-breuninger-testsupport`: Test support for feature toggles plus utilities.
* [`spring-boot-starter-breuninger-validation`](spring-boot-starter-breuninger-validation/README.md): Optional module for validation in Spring with a specific response format.

Examples:
* [`example-status`](examples/example-status): Service only relying on `edison-core` to show the usage of health and status features. 
* [`example-metrics`](examples/example-metrics): Service that is using edison-core metrics.
* [`example-jobs`](examples/example-jobs): Edison service using edison-jobs to run background tasks. 
* [`example-togglz`](examples/example-togglz): Example using `edison-togglz´ to implement feature toggles.
* [`example-togglz-mongo`](examples/example-togglz-mongo): Same `edison-toggz`, but with a MongoDB configuration to auto-configure persistence of 
feature toggles.

## Setup

Make sure you have Java 1.8 or later and gradle 4.x installed on your computer.

### Testing

Test and create coverage report

    gradle check

### Dependency Update

Determine possible dependency updates

    gradle dependencyUpdates -Drevision=release

### Publishing

Publish new releases

    gradle uploadArchives


## Examples

There are a few examples that may help you to start your first microservice based
on Edison and Spring Boot. Because Spring Boot itself has some complexity, it is
recommended to first read it's documentation before starting with Edison.

The examples can be started with gradle:

    gradle examples:example-status:bootRun
    gradle examples:example-metrics:bootRun
    gradle examples:example-jobs:bootRun
    gradle examples:example-togglz:bootRun
    gradle examples:example-togglz-mongo:bootRun

Open in your browser [http://localhost:8080/](http://localhost:8080/)

*Note:* Every example is configured to use port 8080, so make sure to run only one example at a time or to reconfigure
the ports.


## Contributing

Have a look at our [contribution guidelines](CONTRIBUTING.md).
