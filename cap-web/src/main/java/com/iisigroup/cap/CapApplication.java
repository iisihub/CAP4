package com.iisigroup.cap;

import java.util.stream.Stream;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.apache.catalina.core.Constants;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;
import org.tuckey.web.filters.urlrewrite.gzip.GzipFilter;

import com.iisigroup.cap.mvc.action.PageAction;
import com.iisigroup.cap.web.CapHandlerServlet;
import com.iisigroup.cap.web.LogContextFilter;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;

import de.codecentric.boot.admin.config.EnableAdminServer;

@EnableAdminServer
@SpringBootApplication
@ImportResource({ "classpath:spring/applicationContext.xml" }) // , "classpath:spring/security.xml" })
public class CapApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return configureApplication(builder);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CapApplication.class, args);
    }

    private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
        return builder.sources(CapApplication.class).bannerMode(Banner.Mode.OFF);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        Resource[] resources = new ClassPathResource[] { new ClassPathResource("config.properties"), new ClassPathResource("db/database.properties") };
        propertySourcesPlaceholderConfigurer.setLocations(resources);
        propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(false);
        // propertySourcesPlaceholderConfigurer.setSystemPropertiesModeName("SYSTEM_PROPERTIES_MODE_OVERRIDE");
        propertySourcesPlaceholderConfigurer.setIgnoreResourceNotFound(false);
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public ServletRegistrationBean restServlet() {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(PageAction.class);
        DispatcherServlet pageDispatcherServlet = new DispatcherServlet(applicationContext);
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(pageDispatcherServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/page/*");
        // 不設定 name，就會取代預設的 DispatcherServlet，如果設了 name 就會變成兩個 DispatcherServlet 導致無限遞迴...
        // registrationBean.setName("page");
        return registrationBean;
    }

    @Bean
    public ServletRegistrationBean capHandlerServletRegistrationBean() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new CapHandlerServlet(), "/handler/*");
        registrationBean.addInitParameter("pluginManager", "CapPluginManager");
        registrationBean.addInitParameter("defaultRequest", "CapDefaultRequest");
        registrationBean.addInitParameter("errorResult", "CapDefaultErrorResult");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setForceEncoding(true);
        characterEncodingFilter.setEncoding("UTF-8");
        registration.setFilter(characterEncodingFilter);
        registration.setOrder(3);
        return registration;
    }

    @Bean
    public FilterRegistrationBean openEntityManagerInViewFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter openEntityManagerInViewFilter = new OpenEntityManagerInViewFilter();
        registration.setFilter(openEntityManagerInViewFilter);
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setOrder(6);
        return registration;
    }

    @Bean
    public FilterRegistrationBean logContextFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter logContextFilter = new LogContextFilter();
        registration.setFilter(logContextFilter);
        registration.setOrder(5);
        return registration;
    }

    @Bean
    public FilterRegistrationBean urlRewriteFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter urlRewriteFilter = new UrlRewriteFilter();
        registration.setFilter(urlRewriteFilter);
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setOrder(4);
        return registration;
    }

    @Bean
    public FilterRegistrationBean gzipFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter gzipFilter = new GzipFilter();
        registration.setFilter(gzipFilter);
        registration.addUrlPatterns("*.js", "*.css");
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setOrder(2);
        return registration;
    }

    @Bean
    public FilterRegistrationBean pageFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter pageFilter = new SiteMeshFilter();
        registration.setFilter(pageFilter);
        registration.addUrlPatterns("/page/*");
        registration.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
        registration.setOrder(7);
        return registration;
    }

    @Bean
    public FilterRegistrationBean delegatingFilterProxy() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy();
        delegatingFilterProxy.setTargetBeanName(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME);
        registration.setFilter(delegatingFilterProxy);
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public ServletContextInitializer preCompileJspsAtStartup() {
        return servletContext -> {
            getDeepResourcePaths(servletContext, "/WEB-INF/").forEach(jspPath -> {
                // log.info("Registering JSP: {}", jspPath);
                ServletRegistration.Dynamic reg = servletContext.addServlet(jspPath, Constants.JSP_SERVLET_CLASS);
                reg.setInitParameter("jspFile", jspPath);
                reg.setLoadOnStartup(99);
                reg.addMapping(jspPath);
            });
        };
    }

    private static Stream<String> getDeepResourcePaths(ServletContext servletContext, String path) {
        return (path.endsWith("/")) ? servletContext.getResourcePaths(path).stream().flatMap(p -> getDeepResourcePaths(servletContext, p)) : Stream.of(path);
    }
}