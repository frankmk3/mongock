# Getting Started
This project is an example of how to use [Mongock](https://www.mongock.io/) to mantaince MongoDb version changes.

##Apply for specific version
If the migration process needs to be applied for a specific version range Mongock support this approach:

In the `application.properties`
```properties
mongock.start-system-version=2
mongock.end-system-version=3
```
Or using environment variables

```
MONGOCK_START-SYSTEM-VERSION=2
MONGOCK_END-SYSTEM-VERSION=3
```

### Pre requisites ###
- Install java JDK 11.
- Install MongoDb.


#### Installation and Getting started
Run the Gradle command to build and execute the command to start the application.
The database parameters con be overwritten to point to the proper URL.

```
./gradlew build
java -jar build/libs/mongock.jar 
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.0/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.0/gradle-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.5.0/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/docs/2.5.0/reference/htmlsingle/#boot-features-mongodb)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

