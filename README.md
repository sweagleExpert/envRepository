# Spring Cloud Config Server SWEAGLE

Spring Cloud Config Server SWEAGLE enables seamless integration of the regular Spring Cloud Config Server with SWEAGLE to manage external properties for applications across all environments.

# Quick Start
Configure pom.xml, like this:
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server-sweagle</artifactId>
        <version>1.0.0.BUILD-SNAPSHOT</version>
    </dependency>
</dependencies>
```

Create a standard Spring Boot application, like this:
```java
@SpringBootApplication
@EnableSweagleConfigServer
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

TBD...

# References
[spring-cloud-config](https://github.com/spring-cloud/spring-cloud-config)
[sweagle](https://www.sweagle.com/)
