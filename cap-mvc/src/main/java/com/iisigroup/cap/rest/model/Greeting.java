package com.iisigroup.cap.rest.model;

//@XmlRootElement(name = "user")
public class Greeting {

    private final long id;
    private final String content;

    public Greeting() {
        this.id = 0;
        this.content = "";
    }

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    // @XmlElement
    public long getId() {
        return id;
    }

    // @XmlElement
    public String getContent() {
        return content;
    }
}