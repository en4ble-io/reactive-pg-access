# Examples for the reactive-pg-access library

All examples expect a user named 'postgres' with password 'postgres' and the default 'postgres' database.

The database tables are generated from source via a liquibase script on startup.

## Setup

From within the root directory of reactive-pg-access do:

* Create a schema ``pgaccess`` in the ``postgres`` database as user 'postgres'
    * ``psql -c 'create schema pgaccess' postgres postgres``
* build and install reactive-pg-access
    * ``./gradlew clean install && cd -``
* build and install common-persistence (contains common generated db definitions and daos used by all examples)
    * ``cd examples/common && ./gradlew clean install && cd -``

## Running the examples

* (optional) adjust db connection information  

### Micronaut

``cd examples/micronaut && ./gradlew run``

### Spring

``cd examples/spring && ./gradlew bootRun``

### Vert.x

``cd examples/vertx && ./gradlew run``
