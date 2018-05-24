package com.iisigroup.colabase.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author AndyChen
 * @version <ul>
 *          <li>2018/5/11 AndyChen,new
 *          </ul>
 * @since 2018/5/11
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiRequest {
    /**
     * path must follow format ex: person.name
     * @return
     */
    String path() default "";
    String defaultValue() default "";
    boolean noValueNoSend() default false;
}