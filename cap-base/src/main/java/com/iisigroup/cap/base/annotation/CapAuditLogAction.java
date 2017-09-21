package com.iisigroup.cap.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CapAuditLogAction {
    CapActionTypeEnum actionType() default CapActionTypeEnum.UPDATE;

    String action() default "";

    String functionCode() default "";

    String urlPath() default "";

    String name() default "";

    public enum CapActionTypeEnum {
        ADD,
        UPDATE,
        DELETE,
        QUERY,
        EXPORT,
        IMPORT;
    }

}
