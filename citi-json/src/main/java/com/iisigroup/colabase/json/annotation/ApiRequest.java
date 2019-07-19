package com.iisigroup.colabase.json.annotation;

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

    /**
     * 預設要送出的值
     */
    String defaultValue() default "";

    /**
     * 在應用階段field沒有值，且該設定為true，則會刪除該欄位。
     */
    boolean noValueNoSend() default false;

    /**
     * 標示到的屬性，如果在應用階段field沒有值，且該設定為true，則會刪除同階層欄位。
     */
    boolean primaryEmptyClean() default false;
}
