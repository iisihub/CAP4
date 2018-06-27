# CAP for Spring Framework 4.x

## Migrate to Spring 4.3.18 note
### Please note that several minimum dependency versions have been raised: *Jetty 9.1+*, *Jackson 2.6+*, *FreeMarker 2.3.21+*, XStream 1.4.5+. Spring's support for *Hibernate 3.x and Velocity* has been deprecated and scheduled for removal in 5.0.
### As of 4.3, Spring MVC processes HEAD and OPTIONS requests by default if there are no explicit bindings for those HTTP methods on a given path, along the lines of what HttpServlet does by default. While this should be a reasonable enhancement to all common Spring web applications, there may be subtle interaction side effects.

### Web Improvements. https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/htmlsingle/#v4_3-Web-Improvements

### Testing Improvements. https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/htmlsingle/#v4_3-Testing-Improvements

### Using Log4j 1.2 or 2.x. https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/htmlsingle/#overview-logging-log4j

### Avoiding Commons Logging. https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/htmlsingle/#overview-avoiding-commons-logging

### Jackson JSONP Support. https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/htmlsingle/#mvc-ann-jsonp As of Spring Framework 4.3.18, JSONP support is deprecated and will be removed as of Spring Framework 5.1, CORS should be used instead.

### JSON Mapping View. https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/htmlsingle/#view-json-mapping