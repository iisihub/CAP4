package com.iisigroup.colabase.model;

import com.google.gson.JsonObject;
import com.iisigroup.colabase.annotation.ApiRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2018/9/27 AndyChen,new
 * </ul>
 * @since 2018/9/27
 */
public abstract class PostFormData extends RequestContent {

    private final static Logger logger = LoggerFactory.getLogger(PostFormData.class);

    public PostFormData() {
        super();
        Map<String, List<String>> requestHeaders = getRequestHeaders();
        //加入post form要用的header
        ArrayList<String> list = new ArrayList<>();
        list.add("application/x-www-form-urlencoded");
        requestHeaders.put("Content-Type", list);

        JsonObject requestContent = super.getRequestContent();
        if(requestContent == null)
            super.setRequestContent(new JsonObject());
    }

    private Map<String, String> dataMap = new LinkedHashMap<>();

    /**
     * 底層取得送出資料前會先call this method, merge 子代中有掛@ApiRequest的field，存於dataMap
     * @return data map
     */
    public Map<String, String> getDataMap() {
        Class<? extends PostFormData> aClass = getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            ApiRequest annotation = declaredField.getAnnotation(ApiRequest.class);
            if(annotation != null) {
                declaredField.setAccessible(true);
                String key = declaredField.getName();
                String value = "";
                try {
                    value = String.valueOf(declaredField.get(this));
                } catch (IllegalAccessException e) {
                    logger.error("[getDataMap] can NOT get {}'s value", key);
                }
                this.dataMap.put(key, value);
            }
        }
        return this.dataMap;
    }

    /**
     * 避免call父類getJsonString因沒代理，出錯。
     * @return original RequestContent toString
     */
    @Override
    public String getJsonString() {
        return super.getRequestContent().toString();
    }

}
