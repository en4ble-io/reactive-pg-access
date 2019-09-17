# Reactive PostgreSQL Access Layer #

Reactive Database Access Layer for PostgreSQL using [JOOQ](https://jooq.org/) and the top ranking reactiverse [reactive-pg-client](https://vertx.io/docs/vertx-pg-client/kotlin/) (now part of Vert.x).

* Written in Kotlin with support for both RxJava2 and Kotlin Coroutines.
* Generates Database Access Object (DAO), DTO/POJO and database definition classes using the JOOQ generator.
    * DAO and Mappers are generated in Kotlin, DTO in Java 
* Also generates access classes + DTO for database views and adds OpenApi annotations to all DTO.
* Creates openapi and validation annotations from database comments in tables, views and columns 
* transaction support (all methods accept optional client (=transaction) parameter)
* Uses DatabaseContexts to easily handle connections with multiple databases.
* Provides TypedEnums + Converters for storing Enum values that do not depend on the name or ordinal of an enum.
* All mapping and DAO code is generated for easy debugging
* Generated code does not rely on reflection
* Tested with Vert.x, Spring and Micronaut
 
## Usage

For full code examples with **Vert.x**, **Spring** and **Micronaut** please have a look at the projects in the [examples folder](examples).


### Database setup

You can add comments to database columns (tables and views) to get openapi and validation annotations in the generated DTO.

* comment contains {{minlength=<int>}} - generates ``@org.hibernate.validator.constraints.Length(min=<int>)``
* comment contains {{maxlength=<int>}} - generates ``@org.hibernate.validator.constraints.Length(max=<int>)`` 
* comment contains {{email}} - generates ``@javax.validation.constraints.Email``
* comment contains {{default=<string>}} - generates ``defaultValue="your value"`` - Use this with TypedEnum columns to override the database default value.
* comment contains {{readOnly}} - generates ``accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY``
* comment contains {{writeOnly}} - generates ``accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.WRITE_ONLY``
* comment contains {{internal}} - generates ``@io.swagger.v3.oas.annotations.Hidden`` + ``@com.fasterxml.jackson.annotation.JsonIgnore`` - Use this to hide fields from the public API definitions.
* column name starts with 'internal_' - same as {{internal}} but can be used e.g. in database views to clearly indicate which fields will not be included in the public API.

### Code Setup

1. Setup jOOQ via your build tool
2. run jooqGenerate
3. Implement DAO classes by extending the base dao classes
    * jOOQ generates one DAO per table or view.
4. Create a DatabaseContext instance
5. Instantiate your DAO class using the database context.

All DAO classes contain the methods listed below, both in a Coroutine/suspendable and a Reactive version (with an rx prefix).

For example:

* Kotlin coroutine: ``readById``
* RxJava2: ``rxReadById``

For brevity the examples below list only the suspendable variants. 

### DAO methods

#### Create

Creates a new row in the database table that associated with the current DAO.

* ``create(dto)::Int``: Create the row and return 1 (the number of affected rows) on success.
* ``createReturning(dto)::DTO``: Create the row and return the new entry containing all columns (including generated/default values)


#### Read

Read data from the database table or view that associated with the current DAO.

* ``read``: Return a list of entries  
* ``readOne``: Return one entry or throw io.en4ble.pgaccess.exceptions.NoResultsException if no matching entry was found.
* ``readOptional``: Return an Optional containing either one or none entry.
* ``readBy``: Convenience methods for single parameter queries

Available methods:

* ``read(Query/Condition)::List<DTO>``
* ``readOne(Query/Condition)::DTO``
* ``readOptional(Query/Condition)::Optional<DTO>``

* ``readBy{Attribute}::List<DTO>``
* ``readOneBy{Attribute}::DTO``
* ``readOptionalBy{Attribute}::Optional<DTO>``

#### Update

Update entries of the database table that associated with the current DAO.

* ``update(dto, condition)::Int``: update the row(s) and return the number of affected rows.
* ``updateReturning(dto, condition)::DTO``: update the row and return the updated entry
 
* ``update(pair1,pair2 ..., condition)::Int``
* ``updateReturning(pair1,pair2 ..., condition)::DTO``

#### Delete

Delete entries of the database table that associated with the current DAO.

* ``delete(condition)::Int``

### Custom enum converters

The standard enum converter of jOOQ allows us to either store the enum by name or ordinal, which are both solutions that
can lead to issues in case of a code refactoring.

In order to increase both flexibility and stability in dealing with enums, reactive-pg-access provides the special base class 
TypedEnumConverter with 2 default implementations for String and Integer based enums which can be extended to handle custom types.

To use this mechanism, all you need to do is let your enum extend one of these interfaces: 

* ``io.en4ble.pgaccess.enumerations.TypedEnum``
* ``io.en4ble.pgaccess.enumerations.StringEnum``
* ``io.en4ble.pgaccess.enumerations.IntEnum``

And then implement a converter for your enum that extends one of these abstract classes:

* ``io.en4ble.pgaccess.converters.TypedEnumConverter``
* ``io.en4ble.pgaccess.converters.StringEnumConverter``
* ``io.en4ble.pgaccess.converters.IntEnumConverter``

The converter must then be mapped to the respective column of your database. 
For details on this please see the excellent jOOQ documentation.


**NOTE**: When using typed enums, the specified values will by default only be used with the database - a REST API (via Jackson) will 
use the enum names if you don't add custom (de)serialization. That also means that you have to add custom ``minLength`` and ``maxLength`` 
annotations that match the lengths of the enum names, so that validation logic does not prevent enum changes. 


#### Examples

##### String enum

database column: ``example_state: varchar(3)``

``
enum class ExampleState(override var key: String) : StringEnum {
    NEW("new"), ACTIVE("act"), DELETED("del")
}
``

``class ExampleStateConverter : StringEnumConverter<ExampleState>(ExampleState::class.java)``

##### Integer enum

database column: ``example_state: integer``

``
enum class ExampleState(override var key: Int) : IntEnum {
    NEW(1), ACTIVE(5), DELETED(99)
}
``

``class ExampleStateConverter : IntEnumConverter<ExampleState>(ExampleState::class.java)``

## Contributing ##

This project is work in progress and currently only supports the features that I need and find the time to implement.

If you're interested in extending / improving this project, please contact me at mark@en4ble.io.

### TODO

* generate fromJson / toJson + "JsonPOJO/DTO" interface for DTOs so services do not need to rely on reflection.
    * generate serializer + deserializers and register them with jackson
    * avoid reflection
* add "readonly" to id fields
* Improve example projects with second related table and a view on both.
* add optional connection/transaction parameter to update functions
* refactor custom type handling so that Point can be replaced by the user
* write tests
* Improve documentation:
    * Javadoc
    * README 
