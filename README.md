# DbServer
Exploring eventing in H2 database server mode

## Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/maven-plugin/)
* [Spring Data JDBC](https://docs.spring.io/spring-data/jdbc/docs/current/reference/html/)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/reference/htmlsingle/#production-ready)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/reference/htmlsingle/#using-boot-devtools)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Spring for RabbitMQ](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/reference/htmlsingle/#boot-features-amqp)

### Guides
The following guides illustrate how to use some features concretely:

* [Using Spring Data JDBC](https://github.com/spring-projects/spring-data-examples/tree/master/jdbc/basics)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Messaging with RabbitMQ](https://spring.io/guides/gs/messaging-rabbitmq/)

ALTER TABLE AUDIT.EVENTS ADD "TDAY" integer NULL;

UPDATE AUDIT.EVENTS SET TDAY=DATEDIFF(DAY, CAST('1970-01-01' as DATE), TDATE);

ALTER TABLE AUDIT.EVENTS ALTER COLUMN "TDAY" integer NOT NULL;

ALTER TABLE AUDIT.EVENTS DROP PRIMARY KEY;

ALTER TABLE EVENTS DROP COLUMN "TDATE";

ALTER TABLE AUDIT.EVENTS ADD CONSTRAINT PRIMARY_KEY PRIMARY KEY ("TDAY", "TSEQ");

ALTER TABLE AUDIT.EVENTS ADD COLUMN "TCATALOG" VARCHAR NULL;

UPDATE AUDIT.EVENTS SET TCATALOG = 'BIGQUERY';

ALTER TABLE AUDIT.EVENTS ALTER COLUMN "TCATALOG" VARCHAR NOT NULL;

UPDATE AUDIT.EVENTS SET PAYLOAD='{ "newRow" : ' || PAYLOAD || ' }'