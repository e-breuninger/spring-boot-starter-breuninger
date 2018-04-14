# Edison Mongo

MongoDB persistence for spring-boot-starter-breuninger.  

## Usage
 
 *PENDING*

## MongoStatusDetailIndicator

A StatusDetailIndicator is autoconfigured and regularly checks the availability of the MongoDB. The indicator can be
disabled by setting the property `edison.mongo.status.enabled=false`.

## MongoJobRepository

If spring-boot-starter-breuninger-mongo is configured appropriately and spring-boot-starter-breuninger-jobs is in the classpath, a MongoDB implementation
of the JobRepository is automatically configured.

This provides persistence for Jobs, so job information can be gathered in clustered environments.

## MongoTogglzRepository

Similar to MongoJobRepository, a Mongo-implementation of a Togglz StateRepository is auto-configured, if
spring-boot-starter-breuninger-togglz is used in addition to spring-boot-starter-breuninger-mongo.
