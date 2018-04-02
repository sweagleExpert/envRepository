# Spring Cloud Config Server SWEAGLE

Spring Cloud Config Server SWEAGLE enables seamless integration of the regular Spring Cloud Config Server with SWEAGLE to manage external properties for applications across all environments.

# Disclaimer

The current version is a SNAPSHOT for PoC and demo purposes only. Manual action is needed in order to add this artifact to your local Maven repository.
Stay tuned for updates since uploading the artifact to Maven Central will follow-up.

# Quick Start

In order to use Sweagle as your main configuration repository, add the following dependency in your proper pom.xml:
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server-sweagle</artifactId>
        <version>1.0.0.BUILD-SNAPSHOT</version>
    </dependency>
</dependencies>
```

Create a standard Spring Boot application which will act as a Spring Cloud Configuration Server, like this:
```java
@SpringBootApplication
@EnableSweagleConfigServer
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

Adapt your configuration server's application properties according to your Sweagle account. A sample is included [here](https://github.com/sweagleExpert/envRepository/blob/master/src/main/resources/application.yml) 

> In case you already have a working environment, then the only actions needed are:
> - Replace the `@EnableConfigServer` with `@EnableSweagleConfigServer`
> - Change the application's properties according to the Sweagle pre-requisites


# References
- [spring-cloud-config](https://github.com/spring-cloud/spring-cloud-config)
- [SWEAGLE](https://www.sweagle.com/)
