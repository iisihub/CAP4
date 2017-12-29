package com.iisigroup.cap.rest.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iisigroup.cap.rest.model.Greeting;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "greeting", method = RequestMethod.GET)
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "HelloWorld") String name) {
        return getGreeting(name);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "${route.greeting.path:#{null}}", method = RequestMethod.GET)
    public Greeting greetingByName(@PathVariable(name = "name") String name) {
        return getGreeting(name);
    }

    /**
     * Get greeting
     * 
     * @param name
     * @return Greeting
     */
    private Greeting getGreeting(String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
}