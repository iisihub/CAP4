package com.iisigroup.cap.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.iisigroup.cap.base.constants.CapFunctionCode;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CapAuditLogAction {
    CapActionTypeEnum actionType() default CapActionTypeEnum.UPDATE;

    CapFunctionCode functionCode() default CapFunctionCode.F101;

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
